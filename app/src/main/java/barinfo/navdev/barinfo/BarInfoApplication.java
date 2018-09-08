package barinfo.navdev.barinfo;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BarInfoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/raleway_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}