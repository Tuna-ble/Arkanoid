package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;

public abstract class AbstractButton extends AbstractUIElement{
    protected String text;
    protected Font font;
    protected boolean isHovered;
    protected boolean isClicked;

    protected Color backgroundColor;
    protected Color hoverBackgroundColor;
    protected Color strokeColor;
    protected Color hoverStrokeColor;
    protected Color textColor;

    private double timer = 0.0;
    private final double DURATION = 0.5;

    public AbstractButton(double x, double y, double width, double height, String text) {
        super(x, y, width, height);
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
    
    public void handleInput(I_InputProvider inputProvider) {
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

    public abstract void renderDefault(GraphicsContext gc);

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

