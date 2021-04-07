package de.samuelstein.main;

import de.samuelstein.service.CheckInService;
import de.samuelstein.service.QrCodeScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Main {

    public static void main(String[] args) {
        String apiKey = "";
        String eventName = "";
        String systemEndpoint = "";

        final var options = createOptions();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("alf.io cli", options);
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("k")) {
                apiKey = cmd.getOptionValue("k");
            }
            if (cmd.hasOption("e")) {
                eventName = cmd.getOptionValue("e");
            }
            if (cmd.hasOption("u")) {
                systemEndpoint = cmd.getOptionValue("u");
            }
            if (cmd.hasOption("k") && cmd.hasOption("u") && cmd.hasOption("l")) {
                new CheckInService(systemEndpoint, apiKey).getAllEvents();
                System.exit(0);
            }

        } catch (ParseException e) {
            log.error("Error during parameter parsing found: '{}'.\nBye bye.", e.getMessage());
            System.exit(-1);
        }

        log.info("Starting app with endpoint: {} and event: {}.", systemEndpoint, eventName);

        final var qrCodeScanService = new QrCodeScanService();
        final var checkInService = new CheckInService(systemEndpoint, apiKey, eventName);
        qrCodeScanService.addListener(checkInService);

        qrCodeScanService.receiveQrCodes();
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addRequiredOption("k", "key", true, "api key");
        options.addOption("e", "event", true, "event name");
        options.addRequiredOption("u", "url", true, "alf.io system url");
        options.addOption("l", "list", false, "available events");
        return options;
    }
}
