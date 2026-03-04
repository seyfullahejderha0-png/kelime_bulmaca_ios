package studioyes.kelimedunyasi;

import com.badlogic.gdx.Gdx;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.messageui.MFMailComposeResult;
import org.robovm.apple.messageui.MFMailComposeViewController;
import org.robovm.apple.messageui.MFMailComposeViewControllerDelegateAdapter;
import org.robovm.apple.uikit.UIApplication;

import studioyes.kelimedunyasi.util.SupportRequest;

/**
 * iOS destek e-postası gönderici.
 * SupportRequest interface'ini implement eder.
 *
 * MFMailComposeViewController kullanır.
 * MessageUI.framework gereklidir (robovm.xml'de tanımlı).
 */
public class IOSSupportRequest implements SupportRequest {

    private final String supportEmail;
    private final WordConnectGame game;

    public IOSSupportRequest(String supportEmail, WordConnectGame game) {
        this.supportEmail = supportEmail;
        this.game = game;
    }

    @Override
    public void sendSupportEmail() {
        Gdx.app.postRunnable(() -> {
            if (!MFMailComposeViewController.canSendMail()) {
                Gdx.app.log("IOSSupportRequest", "Mail gönderilemez — Mail uygulaması yüklü değil");
                // Fallback: mailto: URL ile aç
                try {
                    String subject = "Word Connect - Support Request";
                    String body    = getDeviceInfo();
                    String urlStr  = "mailto:" + supportEmail
                            + "?subject=" + urlEncode(subject)
                            + "&body=" + urlEncode(body);
                    NSURL url = new NSURL(urlStr);
                    UIApplication.getSharedApplication().openURL(url);
                } catch (Exception e) {
                    Gdx.app.error("IOSSupportRequest", "Fallback mail açılamadı", e);
                }
                return;
            }

            try {
                MFMailComposeViewController mailController = new MFMailComposeViewController();
                mailController.setToRecipients(java.util.Arrays.asList(supportEmail));
                mailController.setSubject("Word Connect - Support Request");
                mailController.setMessageBody(getDeviceInfo(), false);
                mailController.setMailComposeDelegate(new MFMailComposeViewControllerDelegateAdapter() {
                    @Override
                    public void didFinish(MFMailComposeViewController controller, MFMailComposeResult result, NSError error) {
                        controller.dismissViewController(true, null);
                    }
                });

                // Root view controller üzerinden göster
                org.robovm.apple.uikit.UIViewController rootVC =
                        UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
                if (rootVC != null) {
                    rootVC.presentViewController(mailController, true, null);
                }
            } catch (Exception e) {
                Gdx.app.error("IOSSupportRequest", "Mail controller hatası", e);
            }
        });
    }

    private String getDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("Please type your request above\n");
        sb.append("Platform: iOS\n");
        sb.append("App version: ").append(game != null ? game.version : "—").append("\n");
        sb.append("Device: ").append(System.getProperty("os.arch", "unknown")).append("\n");
        return sb.toString();
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            return s;
        }
    }
}
