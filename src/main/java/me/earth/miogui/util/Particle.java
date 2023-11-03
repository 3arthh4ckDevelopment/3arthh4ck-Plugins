package me.earth.miogui.util;

import me.earth.miogui.gui.MioClickGui;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;
import static org.lwjgl.opengl.GL11.*;

public class Particle {

    private float alpha;
    private final Vector2f pos;
    private static final Random random = new Random();
    private float size;
    private final Vector2f velocity;

    public Particle(Vector2f velocity, float x, float y, float size) {
        this.velocity = velocity;
        pos = new Vector2f(x, y);
        this.size = size;
    }

    public static Particle getParticle() {
        Vector2f velocity = new Vector2f((float)(Math.random() * 3.0 - 1.0), (float)(Math.random() * 3.0 - 1.0));
        float x = (float) random.nextInt(Display.getWidth());
        float y = (float) random.nextInt(Display.getHeight());
        float size = (float)(Math.random() * 4.0) + 2.0f;
        return new Particle(velocity, x, y, size);
    }

    public float getAlpha() {
        return alpha;
    }

    public float getDistanceTo(Particle particle) {
        return getDistanceTo(particle.getX(), particle.getY());
    }

    public float getDistanceTo(float f, float f2) {
        return (float) Util.getDistance(getX(), getY(), f, f2);
    }

    public float getSize() {
        return size;
    }

    public float getX() {
        return pos.getX();
    }

    public float getY() {
        return pos.getY();
    }

    public void setX(float f) {
        pos.setX(f);
    }

    public void setY(float f) {
        pos.setY(f);
    }

    public void setup(int delta, float speed) {
        Vector2f pos = this.pos;
        pos.x += velocity.getX() * delta * (speed / 2);

        Vector2f pos2 = this.pos;
        pos2.y += velocity.getY() * delta * (speed / 2);

        if (alpha < 180.0f) {
            alpha += 0.05f * 15;
        }
        if (this.pos.getX() > Display.getWidth()) {
            this.pos.setX(0.0f);
        }
        if (this.pos.getX() < 0.0f) {
            this.pos.setX((float)Display.getWidth());
        }
        if (this.pos.getY() > Display.getHeight()) {
            this.pos.setY(0.0f);
        }
        if (this.pos.getY() < 0.0f) {
            this.pos.setY((float)Display.getHeight());
        }
    }

    public static class Util {

        private final List<Particle> particles;

        public Util(int in) {
            particles = new ArrayList<>();
            addParticle(in);
        }

        public void addParticle(int in) {
            for (int i = 0; i < in; ++i) particles.add(getParticle());
        }

        public static double getDistance(float x, float y, float x1, float y1) {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        }

        private void drawTracer(float f, float f2, float f3, float f4, Color firstColor, Color secondColor, Color thirdColor, float width) {
            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glShadeModel(GL_SMOOTH);

            glColor4f(firstColor.getRed() / 255.0f, firstColor.getGreen() / 255.0f, firstColor.getBlue() / 255.0f, firstColor.getAlpha() / 255.0f);
            glLineWidth(width);

            glBegin(1);
            glVertex2f(f, f2);

            glColor4f(secondColor.getRed() / 255.0f, secondColor.getGreen() / 255.0f, secondColor.getBlue() / 255.0f, secondColor.getAlpha() / 255.0f);

            float y;

            if (f2 >= f4) {
                y = f4 + ((f2 - f4) / 2);
            } else {
                y = f2 + ((f4 - f2) / 2);
            }

            float x;

            if (f >= f3) {
                x = f3 + ((f - f3) /2 );
            } else {
                x = f + ((f3 - f) /2 );
            }

            glVertex2f(x, y);
            glEnd();
            glBegin(1);
            glColor4f(secondColor.getRed() / 255.0f, secondColor.getGreen() / 255.0f, secondColor.getBlue() / 255.0f, secondColor.getAlpha() / 255.0f);
            glVertex2f(x, y);

            glColor4f(thirdColor.getRed() / 255.0f, thirdColor.getGreen() / 255.0f, thirdColor.getBlue() / 255.0f, thirdColor.getAlpha() / 255.0f);

            glVertex2f(f3, f4);
            glEnd();
            glPopMatrix();
        }

        public void drawParticles() {
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            if (mc.currentScreen == null) return;

            for (Particle particle : particles) {

                particle.setup(2, 0.1f);

                int width = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
                int height = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;
                final int maxDistance = 300;

                float alpha = (float) MathHelper.clamp(particle.getAlpha() - particle.getAlpha() / maxDistance * getDistance((float)width, (float)height, particle.getX(), particle.getY()), 0.0f, particle.getAlpha());
                Color color = ColorUtil.injectAlpha(MioClickGui.CLICKGUI.get().colorParticles.getValue() ? ColorUtil.getCurrent() : new Color(0x99C9C5C5), (int) alpha);

                glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

                glPointSize(particle.getSize());
                glBegin(0);
                glVertex2f(particle.getX(), particle.getY());
                glEnd();

                float nearestDistance = 0.0f;
                Particle nearestParticle = null;

                for (Particle secondParticle : particles) {

                    float distance = particle.getDistanceTo(secondParticle);

                    if (distance <= maxDistance && (getDistance((float)width, (float)height, particle.getX(), particle.getY()) <= maxDistance || getDistance((float)width, (float)height, secondParticle.getX(), secondParticle.getY()) <= maxDistance)) {

                        if (nearestDistance > 0.0f && distance > nearestDistance) continue;
                        nearestDistance = distance;
                        nearestParticle = secondParticle;
                    }
                }
                if (nearestParticle == null) continue;

                drawTracer(particle.getX(), particle.getY(), nearestParticle.getX(), nearestParticle.getY(), color, ColorUtil.injectAlpha(new Color(0x838080), (int) alpha), color, 0.6f);
            }

            glPushMatrix();

            glTranslatef(0.5f, 0.5f, 0.5f);
            glNormal3f(0.0f, 1.0f, 0.0f);

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
            glDepthMask(true);
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }
}