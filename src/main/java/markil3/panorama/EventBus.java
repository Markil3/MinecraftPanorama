package markil3.panorama;

import net.minecraft.client.render.Camera;

import markil3.panorama.mixins.CameraAccessor;

public class EventBus
{
    public static void onCamera(Camera event)
    {
        if (Main.isRenderingPanorama())
        {
            switch (Main.getPanoramaStage() - 2)
            {
            default:
            case 0:
                ((CameraAccessor) event).setView(0, 0);
                break;
            case 1:
                ((CameraAccessor) event).setView(90, 0);
                break;
            case 2:
                ((CameraAccessor) event).setView(180, 0);
                break;
            case 3:
                ((CameraAccessor) event).setView(270, 0);
                break;
            case 4:
                ((CameraAccessor) event).setView(0, -90);
                break;
            case 5:
                ((CameraAccessor) event).setView(0, 90);
                break;
            }
        }
    }
}
