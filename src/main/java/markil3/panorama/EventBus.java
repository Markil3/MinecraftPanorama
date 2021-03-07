package markil3.panorama;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EventBus
{
    static int screenshotState = 0;

    @SubscribeEvent
    public static void onScreenshot(ScreenshotEvent event)
    {
//        event.setCanceled(true);
        screenshotState = 1;
    }

    @SubscribeEvent
    public static void onRender(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && screenshotState > 0)
        {
            Main.renderPanorama(Minecraft.getInstance());
            screenshotState = 0;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event)
    {
        if (screenshotState > 0)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFov(EntityViewRenderEvent.FOVModifier event)
    {
        if (screenshotState > 0)
        {
            event.setFOV(90);
        }
    }

    @SubscribeEvent
    public static void onCamera(EntityViewRenderEvent.CameraSetup event)
    {
        if (screenshotState > 0)
        {
            event.setRoll(0);
            switch (screenshotState - 2)
            {
            default:
            case 0:
                event.setYaw(0F);
                event.setPitch(0);
                break;
            case 1:
                event.setYaw(90);
                event.setPitch(0);
                break;
            case 2:
                event.setYaw(180);
                event.setPitch(0);
                break;
            case 3:
                event.setYaw(270);
                event.setPitch(0);
                break;
            case 4:
                event.setYaw(0);
                event.setPitch(-90);
                break;
            case 5:
                event.setYaw(0);
                event.setPitch(90);
                break;
            }
            screenshotState++;
        }
    }
}
