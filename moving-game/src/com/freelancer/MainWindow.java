package com.freelancer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the main (and single) application window.
 * <p>
 * With this window user can select image file name in current directory from
 * dropdown menu and this image will be loaded. Then, window size will be
 * scaled and loaded and drawn image starts randomly moving on the window.
 */
public class MainWindow extends JFrame implements Runnable, ActionListener {
    public MainWindow() throws HeadlessException {
        super(WINDOW_NAME);
        working_directory = new File(".");
        timer = new Timer(DELAY_MS, this);
    }

    @Override
    public void run() {
        setUpComponents();
        timer.start();
    }

    /**
     * Calls when user selects an item (a file name) in the ComboBox or when
     * timer counts down and we should repaint window to randomly change the
     * current position of the image.
     *
     * @param e Action, which was performed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JComboBox) {
            JComboBox jComboBox = (JComboBox) source;
            String selected_file = (String) jComboBox.getSelectedItem();

            this.remove(jComboBox);
            BufferedImage image = loadImage(
                    new File(
                            working_directory.getAbsolutePath() + "/" +
                                    selected_file));
            this.setSize(
                    image.getWidth() * SCALE_FACTOR,
                    image.getHeight() * SCALE_FACTOR);

            JComponent contentPane = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image,
                            new Random().nextInt(image.getWidth() * (SCALE_FACTOR - 1)),
                            new Random().nextInt(image.getHeight() * (SCALE_FACTOR - 1)),
                            image.getWidth(), image.getHeight(),
                            this);
                }
            };

            this.setContentPane(contentPane);
            this.revalidate();
            this.repaint();
        } else if (source == timer) {
            this.revalidate();
            this.repaint();
        }
    }


    /**
     * Initializes application window and adds first component to it - ComboBox
     * with list of image files in current directory.
     */
    private void setUpComponents() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String[] images_list = getImagesList(working_directory);
        JComboBox<String> imageList = new JComboBox<>(images_list);
        imageList.addActionListener(this);
        this.add(imageList);

        this.pack();
        this.setVisible(true);
    }

    /**
     * Retrieves list of image files in given directory.
     *
     * @param directory Where we should search for images.
     * @return Array - list of images in directory.
     */
    private String[] getImagesList(File directory) {
        if (!directory.isDirectory()) {
            return new String[0];
        }

        return directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File file = new File(dir.getPath() + "/" + name);
                if (file.isDirectory()) {
                    return false;
                }
                Pattern image_file_pattern = Pattern.compile(".*\\.(jpg|jpeg|png|bmp)");
                Matcher matcher = image_file_pattern.matcher(name);
                return matcher.matches();
            }
        });
    }

    /**
     * Loads provided image file to memory.
     *
     * @param image {@code File} object for selected image.
     * @return Loaded image.
     */
    private BufferedImage loadImage(File image) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(image);
            if (bufferedImage == null) {
                throw new IOException();
            }
            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read image: " + image.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    private File working_directory;
    private Timer timer;

    private static final String WINDOW_NAME = "Java moving game";
    // Windows dimensions will be more than image dimensions on this
    // scale factor:
    private static final int SCALE_FACTOR = 3;
    // How fast we should change image position:
    private static final int DELAY_MS = 1000;
}
