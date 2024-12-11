package com.gym.gym.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gym.gym.domain.BuyList;
import com.gym.gym.domain.CustomUser;
import com.gym.gym.domain.QRcode;
import com.gym.gym.service.BuyListService;
import com.gym.gym.service.QRCodeGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/generate-qr-code")
public class QRCodeController {

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    private BuyListService buyListService;

    @PostMapping
    public String generateQRCode(@AuthenticationPrincipal CustomUser user, Model model) throws Exception {
        QRcode qrCode = new QRcode();

        Long no = user.getNo();
        qrCode.setNo(no); // Users 테이블에서 no 받은 후 Qrcode에 세팅
        qrCode.setUuid(UUID.randomUUID().toString());

        // 유저가 티켓 보유 중일 시에만 QR페이지 이동

        BuyList buyList = buyListService.lastBuyList(no);
        // 날짜 가져오기

        // 티켓이 없으면
        if (buyList == null) {
            model.addAttribute("errorMessage", "티켓이 없거나 만료되었습니다");

            return "redirect:/user/ticket/choice";
        }

        // 티켓이 만료되었으면
        else {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = buyList.getEndDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (endDateTime.isBefore(currentDateTime)) {
                return "redirect:/user/ticket/choice";
            }
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = buyList.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 티켓이 있으면서, 티켓 만료기간 남았으면
        if (buyList.getUserNo() == user.getNo() && endDateTime.isAfter(currentDateTime)) {

            ByteArrayOutputStream qrCodeOutputStream = new ByteArrayOutputStream();
            try {
                qrCodeGenerator.generateQRCodeImage(qrCode, qrCodeOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "QR 코드 생성 중 오류가 발생했습니다.");
                return "error";
            }

            byte[] imageBytes = qrCodeOutputStream.toByteArray();
            String qrCodeBase64 = Base64.getEncoder().encodeToString(imageBytes);

            // QR 코드 URL을 모델에 추가
            String qrCodeUrl = String.format("http://192.168.30.63:8080/admin/attendance/check?qrcodeId=%d&uuid=%s",
                    qrCode.getNo(), qrCode.getUuid());
            model.addAttribute("qrCodeBase64", qrCodeBase64);
            model.addAttribute("qrCodeUrl", qrCodeUrl); // QR 코드 URL 추가

            return "qrCodePage"; // qrCodePage.html로 반환
        }

        return "redirect:/index";
    }
}
