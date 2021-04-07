package de.samuelstein.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor
public class QrCodeScanService {

    private final Pattern ticketPattern = Pattern.compile("^[^{\\}]+$");
    private final Scanner scannerHID = new Scanner(System.in);
    private final List<QrCodeScanListener> listeners = new ArrayList<>();

    public void addListener(QrCodeScanListener qrCodeScanListener) {
        listeners.add(qrCodeScanListener);
    }

    public void receiveQrCodes() {
        log.info("\nWaiting for qr-codes...");
        var input = scannerHID.nextLine();

        while (!StringUtils.equalsIgnoreCase(input, "q")) {
            if (isValidTicketCode(input)) {
                notifyObservers(input);
                input = scannerHID.nextLine();
            }
        }
        log.info("Quit was pressed. Stopping the app");
        System.exit(0);
    }

    private void notifyObservers(String input) {
        for (QrCodeScanListener listener : listeners) {
            listener.checkIn(input);
        }
    }

    private boolean isValidTicketCode(String input) {
        return ticketPattern.matcher(input).matches();
    }
}
