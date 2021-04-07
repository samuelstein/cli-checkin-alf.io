import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static de.samuelstein.service.QrCodeScanService.KEYMAP;

class QrCodeScanServiceTest {

    @Test
    void testKeyboardLayout() {
//        final var it = InputContext.getInstance();
//        it.selectInputMethod(Locale.ENGLISH);

        final var usKeyStrokes = "66d934a6ßc1d1ß4e07ßafb3ßf432b7dae381-Ak0`Vyigd4X02cpXKmZz8p1T-IPz8OoMVN9cRtRXaU4";

//        Stream.of(usKeyStrokes)
//                .map(KeyEvent::getKeyText)

        var bla = usKeyStrokes.chars()
//                .mapToObj(KeyEvent::getExtendedKeyCodeForChar)
                .mapToObj(c -> String.valueOf((char) c))
//                .mapToObj(k -> KeyStroke.getKeyStroke(k, 0).getKeyChar())
//                .mapToObj(c -> String.valueOf((char) c))
//                .map(String::valueOf)
                .map(k -> this.convert(k))
                .collect(Collectors.joining());
        System.out.println(bla);
    }

    private String convert(String s){
        if(KEYMAP.containsKey(s)){
            return KEYMAP.get(s);
        }else{
            return s;
        }
    }
}
