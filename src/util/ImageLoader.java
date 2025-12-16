package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// 이미지 리소스를 로드하고 관리하는 유틸리티 클래스
// 이미지를 캐싱하여 재사용성을 높이고, 로드 실패 시 대체 이미지 생성
public class ImageLoader {

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    // 지정된 경로의 이미지 로드
    public static BufferedImage getImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        BufferedImage image = null;
        try {
            // resources/images 폴더에서 이미지 검색
            URL url = ImageLoader.class.getClassLoader().getResource("resources/images/" + path);
            if (url != null) {
                image = ImageIO.read(url);
            } else {
                System.err.println("Could not find image: " + path);
                image = createPlaceholderImage(path);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + path + e.getMessage());
            image = createPlaceholderImage(path);
        }

        if (image != null) {
            imageCache.put(path, image);
        }
        return image;
    }

    // 이미지를 찾을 수 없을 때 보여줄 임시 이미지 생성
    private static BufferedImage createPlaceholderImage(String path) {
        // 이미지 용도에 따라 크기와 색상을 다르게 설정
        int width = 50;
        int height = 50;
        Color color = Color.MAGENTA; // 기본 에러 색상

        if (path.contains("bg") || path.contains("background")) {
            width = 800;
            height = 600;
            color = Color.DARK_GRAY;
        } else if (path.contains("player") || path.contains("earth")) {
            width = 100;
            height = 100;
            color = Color.BLUE;
        } else if (path.contains("star") || path.contains("meteor")) {
            width = 40;
            height = 40;
            color = Color.YELLOW;
        }

        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.drawString("IMG", 5, height / 2);
        g2d.dispose();
        return placeholder;
    }
}