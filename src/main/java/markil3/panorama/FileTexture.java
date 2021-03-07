package markil3.panorama;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTexture extends SimpleTexture
{
    private final File file;

    public FileTexture(ResourceLocation p_i1275_1_, File file)
    {
        super(p_i1275_1_);
        this.file = file;
    }

    @Override
    protected TextureData getTextureData(IResourceManager p_215246_1_)
    {
        return getTextureData(p_215246_1_, this.file);
    }

    public static SimpleTexture.TextureData getTextureData(IResourceManager resourceManagerIn,
                                                           File locationIn)
    {
        NativeImage image;
        TextureMetadataSection meta;
        try (FileInputStream stream = new FileInputStream(locationIn))
        {
            image =
                    NativeImage.read(stream);
            meta = null;

            return new SimpleTexture.TextureData(null,
                    image);
        }
        catch (IOException e)
        {
            return new SimpleTexture.TextureData(e);
        }
    }
}
