package markil3.panorama;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTexture extends ResourceTexture
{
    private final File file;

    public FileTexture(Identifier p_i1275_1_, File file)
    {
        super(p_i1275_1_);
        this.file = file;
    }

    @Override
    protected ResourceTexture.TextureData loadTextureData(ResourceManager p_215246_1_)
    {
        return getTextureData(p_215246_1_, this.file);
    }

    public static ResourceTexture.TextureData getTextureData(ResourceManager resourceManagerIn,
                                                           File locationIn)
    {
        NativeImage image;
        TextureResourceMetadata meta;
        try (FileInputStream stream = new FileInputStream(locationIn))
        {
            image =
                    NativeImage.read(stream);
            meta = null;

            return new ResourceTexture.TextureData(null,
                    image);
        }
        catch (IOException e)
        {
            return new ResourceTexture.TextureData(e);
        }
    }
}
