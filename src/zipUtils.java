import com.google.common.collect.Lists;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.CollectionUtils;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.zip.ZipEntry.STORED;

public class zipUtils {

    public static List<String> unzip(final File zipFile, final String destination, String encoding,
                                     Map<String, ZipEntry> zipEntryMethodMap) throws Exception{
        return unzip(zipFile, destination, encoding, zipEntryMethodMap, false);
    }

    public static List<String> unzip(final File zipFile, final String destination, String encoding,
                                     Map<String, ZipEntry> zipEntryMethodMap, boolean isRelativePath) throws Exception{
        List<String> fileNames = new ArrayList<String>();
        String dest = destination;
        if (!destination.endsWith(File.separator)) {
            dest = destination + File.separator;
        }
        ZipFile file;
        try {
            file = null;
            if (null == encoding) {
                file = new ZipFile(zipFile);
            } else {
                file = new ZipFile(zipFile, encoding);
            }
            Enumeration<ZipArchiveEntry> en = file.getEntries();
            ZipArchiveEntry ze = null;
            while (en.hasMoreElements()) {
                ze = en.nextElement();
                File f = new File(dest, ze.getName());
                if (ze.isDirectory()) {
                    f.mkdirs();
                    continue;
                } else {
                    f.getParentFile().mkdirs();
                    InputStream is = file.getInputStream(ze);
                    OutputStream os = new FileOutputStream(f);
                    IOUtils.copy(is, os);
                    is.close();
                    os.close();
                    fileNames.add(f.getAbsolutePath());
                    if (zipEntryMethodMap != null && ze.getMethod() == STORED) {
                        zipEntryMethodMap.put(isRelativePath ? ze.getName() : f.getAbsolutePath(), ze);
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = real.getName();
        while (true) {
            real = real.getParentFile();
            if (real == null) {
                break;
            }
            if (real.equals(base)) {
                break;
            } else {
                ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }

    private static List<File> getSubFiles(File baseDir) {
        List ret = new ArrayList();
        File[] tmp = baseDir.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].isFile()) {
                ret.add(tmp[i]);
            }
            if (tmp[i].isDirectory()) {
                ret.addAll(getSubFiles(tmp[i]));
            }
        }
        return ret;
    }

    private static String relative(File srcDir, File file) {
        final Path relativize = srcDir.toPath().relativize(file.toPath());
        final String toString = relativize.toString();
        return toString;
    }

    public static void rezip(File output, File srcDir, Map<String, ZipEntry> zipEntryMethodMap, List<String> topRank) throws IOException {
        System.out.println("topRank.size:" + topRank.size());
        if (output.isDirectory()) {
            throw new IOException("This is a directory!");
        }
        if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }

        if (!output.exists()) {
            output.createNewFile();
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        int readLen = 0;
        List<File> fileList = getSubFiles(srcDir).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        final Predicate predicate = item -> {
            File file = (File) item;
            final String toString = relative(srcDir, file);
            return topRank.contains(toString);
        };
        List<File> collection = fileList.stream()
                .filter(item -> predicate.evaluate(item))
                .sorted(Comparator.comparing(item -> topRank.indexOf(relative(srcDir, item))))
                .collect(Collectors.toList());

        final Collection selectRejected = CollectionUtils.isEmpty(topRank) ? fileList : CollectionUtils.selectRejected(fileList, predicate);
        collection.addAll(selectRejected);
        for (Object o : collection) {
            File f = (File)o;
            ze = new ZipEntry(getAbsFileName(srcDir.getPath(), f));
            ze.setSize(f.length());
            ze.setTime(f.lastModified());
            if (zipEntryMethodMap != null) {
                ZipEntry originEntry = zipEntryMethodMap.get(ze.getName());
                if (originEntry != null) {
                    if (originEntry.getMethod() == STORED) {
                        ze.setCompressedSize(f.length());
                        InputStream in = new BufferedInputStream(new FileInputStream(f));
                        try {
                            CRC32 crc = new CRC32();
                            int c;
                            while ((c = in.read()) != -1) {
                                crc.update(c);
                            }
                            ze.setCrc(crc.getValue());
                        } finally {
                            in.close();
                        }
                    }
                    ze.setMethod(originEntry.getMethod());
                }
            }
            zos.putNextEntry(ze);
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                zos.write(buf, 0, readLen);
            }
            is.close();
        }
        zos.close();
    }
}
