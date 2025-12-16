package util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// 게임 사운드를 관리하는 유틸리티 클래스
// 효과음 재생 및 배경음악 반복 재생 기능 제공
public class SoundManager {

    // 사운드 클립 캐시 (한번 로드한 사운드는 재사용)
    private static final Map<String, Clip> soundCache = new HashMap<>();
    
    // 현재 재생 중인 배경음악 클립
    private static Clip bgmClip;
    private static String currentBgmFileName; // 현재 설정된 BGM 파일명
    private static boolean isMuted = false;

    // 음소거 설정
    public static void setMuted(boolean muted) {
        isMuted = muted;
        if (isMuted) {
            stopBGM(); // 음소거 시 BGM 정지
        } else {
            // 음소거 해제 시 이전에 재생하던 BGM이 있다면 다시 재생
            if (currentBgmFileName != null) {
                playBGM(currentBgmFileName);
            }
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }

    // 효과음 재생
    public static void playEffect(String fileName) {
        if (isMuted) return; // 음소거 상태면 재생 안 함

        try {
            Clip clip = getClip(fileName);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop(); // 이미 재생 중이면 정지
                }
                clip.setFramePosition(0); // 처음으로 되감기
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("효과음 재생 실패: " + fileName + " - " + e.getMessage());
        }
    }

    // 배경음악 무한 반복 재생
    public static void playBGM(String fileName) {
        currentBgmFileName = fileName; // 현재 BGM 기억
        if (isMuted) return; // 음소거면 재생 안 함

        stopBGM(); // 기존 BGM 정지
        try {
            Clip clip = getClip(fileName);
            if (clip != null) {
                bgmClip = clip;
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // 무한 반복
            }
        } catch (Exception e) {
            System.err.println("BGM 재생 실패: " + fileName + " - " + e.getMessage());
        }
    }

    // 재생 중인 배경음악 정지
    public static void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    // 사운드 파일을 로드하여 Clip 객체 반환
    private static Clip getClip(String fileName) {
        if (soundCache.containsKey(fileName)) {
            return soundCache.get(fileName);
        }

        try {
            // 리소스 경로에서 스트림 열기
            // 경로가 /로 시작하면 절대 경로(classpath 기준), 아니면 상대 경로
            String path = fileName.startsWith("/") ? fileName : "/resources/sounds/" + fileName;
            InputStream is = SoundManager.class.getResourceAsStream(path);
            
            if (is == null) {
                // fallback: sounds 폴더 직접 참조 시도
                is = SoundManager.class.getResourceAsStream("/sounds/" + fileName);
            }
            
            if (is == null) {
                System.err.println("사운드 파일 찾을 수 없음: " + fileName);
                return null;
            }

            // 오디오 입력 스트림 생성
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            
            soundCache.put(fileName, clip);
            return clip;
        } catch (Exception e) {
            System.err.println("오디오 로드 실패 (" + fileName + "): " + e.getMessage());
            return null;
        }
    }
}