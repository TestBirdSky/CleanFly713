package vjuz.quinoa.bijzk;

import android.content.Intent;

import com.raven.tabor.TaborHelper;
import com.raven.tabor.core.BaseRavenSer;


/**
 * Date：2025/10/11
 * Describe:
 * vjuz.quinoa.bijzk.SerRaven
 */
public class SerRaven extends BaseRavenSer {
    @Override
    public void onCreate() {
        super.onCreate();
        TaborHelper.INSTANCE.setSuccessNoti(true);
    }
}
