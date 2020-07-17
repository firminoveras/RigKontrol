package com.firmino.rigkontrol.midi;

import android.content.Context;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

public class MidiKontroller {

    public static final int ERRO_NOT_CONNECTED = 0;
    public static final int ERRO_IO_EXCEPTION = 1;

    public static final int MIDI_CONNECTION_SUCESS = 0;
    public static final int MIDI_CONNECTION_ERRO = 1;
    public static final int MIDI_DISCONNECTION_SUCESS = 2;
    public static final int MIDI_DISCONNECTION_ERRO = 3;

    private final MidiManager mMidi;
    private boolean isConnected = false;
    private int mChannel;
    private MidiInputPort mMidiPort;
    private OnMidiMessageSendListener mOnMidiMessageSendListener;
    private OnMidiConnectionStatusChangedListener mOnMidiConnectionStatusChangedListener;

    public MidiKontroller(Context context) {
        mMidi = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
    }

    public MidiManager getMidi() {
        return mMidi;
    }

    public boolean connect(int channel) {
        disconnect();
        mChannel = channel;
        if (mMidi != null) {
            try {
                mMidi.openDevice(mMidi.getDevices()[0], device -> mMidiPort = device.openInputPort(0), new Handler(Looper.getMainLooper()));
                isConnected = true;
                mOnMidiConnectionStatusChangedListener.onMidiConnectionChanged(true, MIDI_CONNECTION_SUCESS);
            }catch (Exception ex){
                mOnMidiConnectionStatusChangedListener.onMidiConnectionChanged(false, MIDI_CONNECTION_ERRO);
            }
        } else {
            mOnMidiConnectionStatusChangedListener.onMidiConnectionChanged(false, MIDI_CONNECTION_ERRO);
        }
        return isConnected;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                mMidiPort.close();
                mMidiPort = null;
                isConnected = false;
                mOnMidiConnectionStatusChangedListener.onMidiConnectionChanged(false, MIDI_DISCONNECTION_SUCESS);
            } catch (IOException e) {
                mOnMidiConnectionStatusChangedListener.onMidiConnectionChanged(false, MIDI_DISCONNECTION_ERRO);
            }
        }
    }

    public void sendControlChange(int control, int value) {
        if (isConnected) {
            try {
                mMidiPort.send(new byte[]{
                        (byte) (0xB0 + (mChannel - 1)),
                        (byte) control,
                        (byte) value
                }, 0, 3);
                if (mOnMidiMessageSendListener != null) {
                    mOnMidiMessageSendListener.onMidiMessageSendSucess(mChannel, control, value);
                }
            } catch (IOException e) {
                if (mOnMidiMessageSendListener != null) {
                    mOnMidiMessageSendListener.onMidiMessageSendFailed(mChannel, control, value, ERRO_IO_EXCEPTION);
                }
            }
        } else {
            if (mOnMidiMessageSendListener != null) {
                mOnMidiMessageSendListener.onMidiMessageSendFailed(mChannel, control, value, ERRO_NOT_CONNECTED);
            }
        }
    }

    public void setOnMidiMessageSendListener(OnMidiMessageSendListener listener) {
        mOnMidiMessageSendListener = listener;
    }

    public void setOnMidiConnectionChangedListener(OnMidiConnectionStatusChangedListener listener) {
        mOnMidiConnectionStatusChangedListener = listener;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public interface OnMidiMessageSendListener {
        void onMidiMessageSendSucess(int channel, int control, int value);
        void onMidiMessageSendFailed(int channel, int control, int value, int erroId);
    }

    public interface OnMidiConnectionStatusChangedListener {
        void onMidiConnectionChanged(boolean isConnected, int flag);
    }
}
