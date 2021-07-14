import java.io.*;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RezipApk {

    public static void main(String[] args) {
        final ConcurrentMap<String, ZipEntry> zipEntryMethodMap = Maps.newConcurrentMap();
        final String destination = "/Users/bagen/Downloads/oldAndNew5/unzips";
        try {
            zipUtils.unzip(new File("/Users/bagen/Downloads/oldAndNew5/new.apk"), destination, null, zipEntryMethodMap);
            zipUtils.rezip(new File("/Users/bagen/Downloads/oldAndNew5/zips.apk"), new File(destination), zipEntryMethodMap, Lists.newArrayList("AndroidManifest.xml",
                    "HMSCore-availableupdate.properties",
                    "HMSCore-base.properties",
                    "HMSCore-device.properties",
                    "HMSCore-hatool.properties",
                    "HMSCore-log.properties",
                    "HMSCore-stats.properties",
                    "HMSCore-ui.properties",
                    "res/drawable/abc_vector_test.xml",
                    "res/drawable/home_indicator_dot_n_normal.xml",
                    "res/drawable/home_indicator_dot_n_select.xml",
                    "res/drawable/vase_bg_theatre_favored_v2.xml",
                    "res/drawable/vase_bg_theatre_favor_v2.xml",
                    "res/layout/abc_screen_simple.xml",
                    "res/layout/abc_screen_content_include.xml",
                    "res/drawable/yk_top_bg_layer.xml",
                    "res/drawable-xxhdpi-v4/yk_top_bg_new.png",
                    "res/drawable-xxxhdpi-v4/home_default_avatar.png",
                    "res/layout/home_top_tool_bar_v2.xml",
                    "res/drawable/home_top_tool_corner_bg_filter.xml",
                    "res/drawable/home_top_tool_corner_bg_shadow.png",
                    "res/drawable/home_top_tool_corner_bg_new.xml",
                    "res/layout/home_search_frame2.xml",
                    "res/drawable-xxhdpi-v4/home_search_icon.png",
                    "res/drawable/home_top_tool_corner_bg_style.xml",
                    "res/drawable-xxxhdpi-v4/channel_entry.png",
                    "res/layout/home_title_new_arch_item2.xml",
                    "res/drawable/welcome_bg.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
