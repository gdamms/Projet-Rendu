
import algebra.*;
import java.awt.*;

/**
 * Simple shader that just copy the interpolated color to the screen,
 * taking the depth of the fragment into acount.
 * 
 * @author: cdehais
 */
public class PainterShader extends Shader {

    DepthBuffer depth;

    public PainterShader(GraphicsWrapper screen) {
        super(screen);
        depth = new DepthBuffer(screen.getWidth(), screen.getHeight());
    }

    public void shade(Fragment fragment) {
        if (depth.testFragment(fragment)) {
            if (Renderer.thong && Renderer.lightingEnabled) {
                double[] color = { fragment.getAttribute(1), fragment.getAttribute(2), fragment.getAttribute(3) };

                double[] material = Renderer.scene.getMaterial();
                double[] litColor = Renderer.lighting.applyLights(
                        new Vector3(fragment.getAttribute(9), fragment.getAttribute(10), fragment.getAttribute(11)),
                        fragment.getNormal(),
                        color,
                        Renderer.scene.getCameraPosition(),
                        material[0], material[1], material[2], material[3]);

                screen.setPixel(fragment.getX(), fragment.getY(),
                        new Color(
                                (int) Math.min(255, Math.max(255 * litColor[0], 0)),
                                (int) Math.min(255, Math.max(255 * litColor[1], 0)),
                                (int) Math.min(255, Math.max(255 * litColor[2], 0))));

                depth.writeFragment(fragment);
            } else {
                screen.setPixel(fragment.getX(), fragment.getY(), fragment.getColor());
                depth.writeFragment(fragment);
            }
        }
    }

    public void reset() {
        depth.clear();
    }
}
