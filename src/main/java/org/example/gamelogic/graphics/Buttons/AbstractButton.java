package org.example.gamelogic.graphics.Buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;

public abstract class AbstractButton {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected String text;
    protected Font font;
    protected boolean isHovered;
    protected boolean isClicked;
    
    // Colors
    protected Color backgroundColor;
    protected Color hoverBackgroundColor;
    protected Color strokeColor;
    protected Color hoverStrokeColor;
    protected Color textColor;

    public AbstractButton(double x, double y, double width, double height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.font = AssetManager.getInstance().getFont("Anxel", 25);
        this.isHovered = false;
        this.isClicked = false;
        
        // Default colors
        this.backgroundColor = Color.color(0.13, 0.13, 0.13, 0.5);
        this.hoverBackgroundColor = Color.color(0.2, 0.2, 0.2, 0.7);
        this.strokeColor = Color.color(1, 1, 1, 0.8);
        this.hoverStrokeColor = Color.WHITE;
        this.textColor = Color.WHITE;
    }
    
    public void update(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            isHovered = false;
            isClicked = false;
            return;
        }
        
        int mouseX = inputProvider.getMouseX();
        int mouseY = inputProvider.getMouseY();
        
        isHovered = mouseX >= x && mouseX <= x + width &&
                   mouseY >= y && mouseY <= y + height;

        isClicked = isHovered && inputProvider.isMouseClicked();
    }
    
    public abstract void render(GraphicsContext gc);

    public boolean isHovered() {
        return isHovered;
    }
    
    public boolean isClicked() {
        return isClicked;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
               mouseY >= y && mouseY <= y + height;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public void setColors(Color backgroundColor, Color hoverBackgroundColor, 
                         Color strokeColor, Color hoverStrokeColor, Color textColor) {
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.strokeColor = strokeColor;
        this.hoverStrokeColor = hoverStrokeColor;
        this.textColor = textColor;
    }
}

