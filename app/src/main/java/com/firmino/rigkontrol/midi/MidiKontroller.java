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

    private MidiManager sMidi;
    private boolean isConnected = false;
    private int sChannel;
    private MidiInputPort sMidiPort;
    private OnMidiMessageSendListener sOnMidiMessageSendListener;
    private Context mContext;

    public MidiKontroller(Context context) {
        sMidi = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        mContext = context;
    }

    public MidiManager getMidi() {
        return sMidi;
    }

    public void connect(int channel) {
        disconnect();
        sChannel = channel;
        if (sMidi != null) {
            try {
                sMidi.openDevice(sMidi.getDevices()[0], device -> sMidiPort = device.openInputPort(0), new Handler(Looper.getMainLooper()));
                isConnected = true;
            } catch (Exception ex) {
                Toast.makeText(mContext, "Não foi possivel conectar ao midi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                sMidiPort.close();
                sMidiPort = null;
                sMidi = null;
                isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendControlChange(int control, int value) {
        if (isConnected) {
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

    public void setOnMidiMessageSendListener(OnMidiMessageSendListener listener) {
        sOnMidiMessageSendListener = listener;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public interface OnMidiMessageSendListener {
        void onMidiMessageSendSucess(int channel, int control, int value);

        void onMidiMessageSendFailed(int channel, int control, int value, int erroId);
    }
}
