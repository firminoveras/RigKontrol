package com.firmino.rigkontrol.midi;

import android.content.Context;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

public class MidiKontroller {

    public static final int ERRO_NOT_CONNECTED = 0;
    public static final int ERRO_IO_EXCEPTION = 1;

    private static MidiManager sMidi;
    private static int sChannel;
    private static MidiInputPort sMidiPort;
    private static OnMidiMessageSendListener sOnMidiMessageSendListener;

    public static void connect(int channel, Context context) {
        if (sMidi == null) {
            sMidi = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
            sChannel = channel;
            if (sMidi != null) {
                try {
                    sMidi.openDevice(sMidi.getDevices()[0], device -> sMidiPort = device.openInputPort(0), new Handler(Looper.getMainLooper()));
                } catch (Exception ex) {
                    Toast.makeText(context, "Não foi possivel conectar ao midi", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void disconnect() {
        try {
            sMidiPort.close();
            sMidiPort = null;
            sMidi = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendControlChange(int control, int value) {
        if (sMidi != null && sMidiPort != null) {
            try {
                sMidiPort.send(new byte[]{
                        (byte) (0xB0 + (sChannel - 1)),
                        (byte) control,
                        (byte) value
                }, 0, 3);
                if (sOnMidiMessageSendListener != null) {
                    sOnMidiMessageSendListener.onMidiMessageSendSucess(sChannel, control, value);
                }
            } catch (IOException e) {
                if (sOnMidiMessageSendListener != null) {
                    sOnMidiMessageSendListener.onMidiMessageSendFailed(sChannel, control, value, ERRO_IO_EXCEPTION);
                }
            }
        } else {
            if (sOnMidiMessageSendListener != null) {
                sOnMidiMessageSendListener.onMidiMessageSendFailed(sChannel, control, value, ERRO_NOT_CONNECTED);
            }
        }
    }

    public static void setOnMidiMessageSendListener(OnMidiMessageSendListener listener) {
        sOnMidiMessageSendListener = listener;
    }
}
