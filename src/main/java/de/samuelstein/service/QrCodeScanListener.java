package de.samuelstein.service;

public interface QrCodeScanListener {
    void checkIn(String qrCodeContent);
}
