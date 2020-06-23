package com.firmino.rigkontrol.midi;

public interface OnMidiMessageSendListener {
    void onMidiMessageSendSucess(int channel, int control, int value);
    void onMidiMessageSendFailed(int channel, int control, int value, int erroId);
}
