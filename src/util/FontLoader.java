package util;

import java.awt.*;
import java.io.InputStream;

// 폰트 리소스를 로드하는 유틸리티 클래스
public class FontLoader {
    public static Font getFont(String fileName, float size) {
        try {
            InputStream is = FontLoader.class.getResourceAsStream("/resources/fonts/" + fileName);
            if (is == null) {
                System.err.println("폰트 찾을 수 없음: " + fileName);
                return new Font("Malgun Gothic", Font.PLAIN, (int)size);
            }

            // 폰트 생성
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            return font.deriveFont(size);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Font("Malgun Gothic", Font.PLAIN, (int)size);
        }
    }
}
