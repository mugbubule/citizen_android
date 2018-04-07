package jonas.emile.agora;

import android.app.Activity;

import com.navispeed.greg.common.Module;

public class AgoraModule implements Module {

    private static final AgoraModule agoraInstance = new AgoraModule();

    public static AgoraModule getInstance() {
        return agoraInstance;
    }

    private AgoraModule() {
    }

    @Override
    public Class<? extends Activity> getMainActivity() {
        return AgoraActivity.class;
    }

    @Override
    public String getName() {
        return "Agora";
    }
}
