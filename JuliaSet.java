import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.io.*;
import java.text.DecimalFormat;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.IntStream;

public class JuliaSet extends JPanel implements AdjustmentListener, MouseListener, MouseMotionListener, MouseWheelListener, ActionListener
{

    JFrame frame;
    double A, B, brightness, hue, saturation, equation, multipler, eye, iterations = 0;
    int blue;
    JLabel aLabel, bLabel, equLabel, satLabel, brightLabel, hueLabel, multLabel, eyeLabel, iterationLabel;
    JScrollBar aBar, bBar, equBar, satBar, brightBar, hueBar, multBar, eyeBar, iterationBar;
    JButton Reset, Save;
    JPanel scrollPanel, labelPanel, bigPanel, buttonPanel;
    DecimalFormat decForm = new DecimalFormat("0.000");
    BufferedImage image;
    final int maxIter = 300;
    private double zoom = 1;
    private double zoomChangeConstant = 0.1;
    int xChange = 0;
    int yChange = 0;
    Point orginPoint;
    int pixelToRender = 1;
    boolean changing = false;
    long milliseconds = 0;
    public JuliaSet()
    {
        frame = new JFrame("Julia Sets!");
        frame.add(this);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(d);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        aBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
        A = aBar.getValue();

        bBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
        B = bBar.getValue();

        equBar = new JScrollBar(JScrollBar.HORIZONTAL, 6, 0, 0, 100);
        equation = equBar.getValue();

        satBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 100);
        saturation = satBar.getValue();

        hueBar = new JScrollBar(JScrollBar.HORIZONTAL, 1, 0, 0, 100);
        hue = hueBar.getValue();

        brightBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 100);
        brightness = brightBar.getValue();

        multBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 3000);
        multipler = multBar.getValue()/1000.0;
//FIX ThIS
        eyeBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 3000);
        eye = eyeBar.getValue()/1000.0;

        iterationBar = new JScrollBar(JScrollBar.HORIZONTAL, 300, 0, 0, 1000);
        iterations = iterationBar.getValue();

        Reset = new JButton("Reset");
        Save = new JButton("Save");

        aBar.addAdjustmentListener(this);
        bBar.addAdjustmentListener(this);
        equBar.addAdjustmentListener(this);
        satBar.addAdjustmentListener(this);
        hueBar.addAdjustmentListener(this);
        brightBar.addAdjustmentListener(this);
        multBar.addAdjustmentListener(this);
        eyeBar.addAdjustmentListener(this);
        iterationBar.addAdjustmentListener(this);
        
        GridLayout grid = new GridLayout(9, 1); 
        aLabel = new JLabel("A: "+decForm.format(aBar.getValue()));
        bLabel = new JLabel("B: "+decForm.format(bBar.getValue()));
        equLabel = new JLabel("Rad:");
        satLabel = new JLabel("Sat:");
        hueLabel = new JLabel("Hue:");
        brightLabel = new JLabel("Bright");
        multLabel = new JLabel("Mult:");
        eyeLabel = new JLabel("Eye:");
        iterationLabel = new JLabel("Iter:");

        labelPanel = new JPanel();
        labelPanel.setLayout(grid);
        labelPanel.add(aLabel);
        labelPanel.add(bLabel);
        labelPanel.add(equLabel);
        labelPanel.add(satLabel);
        labelPanel.add(hueLabel);
        labelPanel.add(brightLabel);
        labelPanel.add(multLabel);
        labelPanel.add(eyeLabel);
        labelPanel.add(iterationLabel);

        scrollPanel = new JPanel();
        scrollPanel.setLayout(grid);
        scrollPanel.add(aBar);
        scrollPanel.add(bBar);
        scrollPanel.add(equBar);
        scrollPanel.add(satBar);
        scrollPanel.add(hueBar);
        scrollPanel.add(brightBar);
        scrollPanel.add(multBar);
        scrollPanel.add(eyeBar);
        scrollPanel.add(iterationBar);      

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2,1));
        Save.addActionListener(this);
        Reset.addActionListener(this);
        buttonPanel.add(Save);
        buttonPanel.add(Reset);

        bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        bigPanel.add(labelPanel, BorderLayout.WEST);
        bigPanel.add(scrollPanel, BorderLayout.CENTER);
        bigPanel.add(buttonPanel, BorderLayout.EAST);
        frame.addMouseWheelListener(this);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        scrollPanel.addMouseListener(this);
        
        frame.add(bigPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        fixpaint paint = new fixpaint();
        paint.start();

    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
       Graphics2D g2d =  (Graphics2D) g;
       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON); 
        g2d.drawImage(drawJulia(), 0, 0, null);
        g = g2d;
    }

    public class fixpaint extends Thread {
        @Override
        public void run() {
            while(true){
            if(milliseconds !=0 && System.currentTimeMillis() - milliseconds > 100){
                milliseconds = 0;
                pixelToRender = 1;
                repaint();
            }
        }
        }
    }

    public void save(){
        
    }
    public BufferedImage drawJulia()
    {
        int w = frame.getWidth();
        int h = frame.getHeight();
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        
        IntStream.range(xChange, w + xChange).parallel().forEach(x -> {
            IntStream.range(yChange, h + yChange).parallel().forEach(y -> {
                if(x % pixelToRender == 0 && y % pixelToRender == 0){
                    float i = (float)iterations;
                    double zx = 1.5 * (x - w) / (zoom*w);
                    double zy = (y - h) / (zoom*w);
                    
                    while(zx*zx + zy*zy < equation && i > 0)
                    {
                        double temp = multipler * zx*zx - zy*zy + A;
                        zy = eye * zx*zy + B;
                        zx = temp;
                        i--;
                    }

                    int c;

                    if (i>0) c = Color.HSBtoRGB((maxIter/i)%1 * (float)hue, (float)saturation, (float)brightness);
                    else c = Color.HSBtoRGB(maxIter/i * (float)hue, (float)saturation,  (float)brightness);
                    image.setRGB(x - xChange, y - yChange, c);
                }
            });
        });

          pixelToRender = 1;

        return image;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == aBar)
        {
            A = aBar.getValue() / 1000.0;
            aLabel.setText("A: "+decForm.format(aBar.getValue()));
        }
        else if (e.getSource() == bBar)
        {
            B = bBar.getValue() / 1000.0;
            bLabel.setText("B: "+decForm.format(bBar.getValue()));
        }
        else if (e.getSource() == brightBar)
        {
                brightness = brightBar.getValue() / 100.0;
                brightLabel.setText("Bright:" + brightBar.getValue());
        }else if (e.getSource() == satBar)
        {
                saturation = satBar.getValue() / 100.0;
                satLabel.setText("Sat:" + satBar.getValue());
        }else if (e.getSource() == hueBar)
        {
                hue = hueBar.getValue();
                hueLabel.setText("Hue:" + hueBar.getValue());
        }else if (e.getSource() == equBar)
        {
                equation = equBar.getValue();
                equLabel.setText("Rad:" + equBar.getValue());
        }else if (e.getSource() == multBar)
        {
                multipler = multBar.getValue()/1000.0;
                multLabel.setText("Mult:" + multBar.getValue()/1000.0);
        }else if (e.getSource() == iterationBar)
        {
                iterations = iterationBar.getValue();
                iterationLabel.setText("Iter:" + iterationBar.getValue());
        }else if (e.getSource() == eyeBar)
        {
                eye = eyeBar.getValue()/1000.0;
                eyeLabel.setText("Eye:" + eyeBar.getValue()/1000.0);
        }
        milliseconds = System.currentTimeMillis();
        pixelToRender =3;
        repaint();
    }

    public static void main (String[]args)
    {
        JuliaSet runner = new JuliaSet();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        xChange += orginPoint.getX() - e.getX();
        yChange += orginPoint.getY() - e.getY();
        orginPoint = e.getPoint(); 
        milliseconds = System.currentTimeMillis();
        pixelToRender =3;
        repaint();
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseClicked(MouseEvent e) {  
       
    }  
    public void mouseEntered(MouseEvent e) {  
       
    }  
    public void mouseExited(MouseEvent e) {  
         
    }  
    public void mousePressed(MouseEvent e) {  
       orginPoint = e.getPoint();
    }  
    public void mouseReleased(MouseEvent e) {  
        orginPoint = null;
        pixelToRender = 1;
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoom += e.getPreciseWheelRotation()*zoomChangeConstant; 
        milliseconds = System.currentTimeMillis();
        pixelToRender =3;
        repaint();
    }  


}