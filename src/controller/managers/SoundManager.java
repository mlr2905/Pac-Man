package controller.managers;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private static SoundManager instance;
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    public void playSound(String soundPath) {
        try {
            InputStream soundStream = getClass().getResourceAsStream(soundPath);
            if (soundStream != null) {
                BufferedInputStream bufferedStream = new BufferedInputStream(soundStream);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + soundPath);
            e.printStackTrace();
        }
    }
}