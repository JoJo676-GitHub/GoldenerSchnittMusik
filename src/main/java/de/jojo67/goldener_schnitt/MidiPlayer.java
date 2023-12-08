package de.jojo67.goldener_schnitt;

import javax.sound.midi.*;
import java.util.ArrayList;

public class MidiPlayer {

    private final double phi = 0.61803399;
    private int phiSteigend = 0;
    private final int velocity = 150; // Lautstärke 150
    private final MidiChannel[] channels;
    private boolean accelerando;
    private int notenwert = 1000;
    private int gespielteNoten;
    private int fibNote = 1;
    private int fibAlt = 0;
    int verschiebung = 4;
    int terz = 0;
    private final int[] tonleiter = {
            59, //H
            60, //C
            62, //D
            64, //E
            65, //F
            67, //G
            69, //A
            71, //H
            72, //C
            74, //D
            76, //E
            77, //F
            79, //G
            81, //A
            83, //H
            84, //C
            86, //D
            88, //E
            89, //F
            91, //G
            93, //A
            95, //H
            96, //C
    };

    public MidiPlayer() throws InterruptedException, MidiUnavailableException {

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        channels = synth.getChannels();
        channels[1].programChange(79); //79

        for (int i = 0; i < 2; i++) {
            fibonacci();
        }

        goldenerSchnitt();

        Thread.sleep(2000);
        synth.close();
    }

    public void goldenerSchnitt() throws InterruptedException {

        double note;
        for (int k = 0; k < 5; k++) {

            if (phiSteigend == 0) {
                note = 8;
            } else {
                note = 2;
            }

            for (int i = 0; i < 4; i++) {

                channels[1].noteOn(tonleiter[(int) (note + 0.5)] - terz, velocity);
                channels[1].noteOn(tonleiter[(int) (note + 0.5)] - terz - 4, velocity);
                channels[1].noteOn(tonleiter[(int) (note + 0.5)] - terz - 7, velocity);

                Thread.sleep(notenwert);

                channels[1].noteOff(tonleiter[(int) (note + 0.5)] - terz, 1000);
                channels[1].noteOff(tonleiter[(int) (note + 0.5)] - terz - 4, 1000);
                channels[1].noteOff(tonleiter[(int) (note + 0.5)] - terz - 7, 1000);

                gespielteNoten++;
                note = note * (phi + phiSteigend);
            }

            terz += 4 * phiSteigend;
            phiSteigend = Math.abs(phiSteigend - 1);
        }
    }

    public void fibonacci() throws InterruptedException {

        int speicher;
        int fibNoteZerlegenSpeicher;
        int tonleiterMitVerschiebung;

        ArrayList<Integer> noten = new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            fibNoteZerlegenSpeicher = fibNote;

            while (fibNoteZerlegenSpeicher > 9) {

                noten.add(fibNoteZerlegenSpeicher % 10);
                fibNoteZerlegenSpeicher /= 10;
            }

            for (int j = noten.size() - 1; j > 0; j--) {

                tonleiterMitVerschiebung = tonleiter[noten.get(j) + verschiebung] - 12;

                channels[1].noteOn(tonleiterMitVerschiebung, velocity);
                channels[1].noteOn(tonleiterMitVerschiebung + 4, velocity);
                channels[1].noteOn(tonleiterMitVerschiebung + 7, velocity);

                Thread.sleep(notenwert);

                channels[1].noteOff(tonleiterMitVerschiebung, 1000);
                channels[1].noteOff(tonleiterMitVerschiebung + 4, 1000);
                channels[1].noteOff(tonleiterMitVerschiebung + 7, 1000);

                gespielteNoten++;
                einflussfaktoren();
            }

            speicher = fibNote;
            fibNote = fibAlt + fibNote;
            fibAlt = speicher;
            phiSteigend = Math.abs(phiSteigend - 1);
        }
    }

    public void einflussfaktoren() {

        if (gespielteNoten % 3 == 0) {
            accelerando = !accelerando;
        }

        tempo();
    }

    public void tempo() {

        if (accelerando) {
            notenwert = (int) ((notenwert * phi) + 0.5);
        } else {
            notenwert = (int) ((notenwert * (phi + 1)) + 0.5);
        }
    }
}

