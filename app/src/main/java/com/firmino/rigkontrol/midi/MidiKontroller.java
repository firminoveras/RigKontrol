package com.firmino.rigkontrol.midi;

import android.content.Context;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

public class MidiKontroller {
    private static MidiManager mMidi;
    private static int mChannel;
    private static MidiInputPort mMidiPort;

    public static void midiConnect(int channel, Context context) {
        if (mMidi == null) {
            mMidi = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
            mChannel = channel;
            if (mMidi != null) {
                mMidi.openDevice(mMidi.getDevices()[0], device -> {
                    mMidiPort = device.openInputPort(0);
                }, new Handler(Looper.getMainLooper()));
            }
        }
    }

    public static void midiDisconnect() {
        try {
            mMidiPort.close();
            mMidiPort = null;
            mMidi = null;
        } catch (IOException e) {
            System.out.println("ERRO");
        }

    }

    public static void midiSendControlChange(int control, int value) {
        if (mMidi != null && mMidiPort != null) {
            byte[] buffer = {
                    (byte) (0xB0 + (mChannel - 1)),
                    (byte) control,
                    (byte) value
            };

            try {
                mMidiPort.send(buffer, 0, 3);
            } catch (IOException e) {
                System.out.println("ERRO");
            }
        }
    }
}
