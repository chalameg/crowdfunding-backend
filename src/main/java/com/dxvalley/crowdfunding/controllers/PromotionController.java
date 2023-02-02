package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.models.Promotion;
import com.dxvalley.crowdfunding.services.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping
    ResponseEntity<?> getPromotion() {
        var result = promotionService.getPromotions();
        if(result.size() == 0) return new ResponseEntity<>("Currently, there is no promotion.", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("getPromotion/{promotionId}")
    ResponseEntity<?> getPromotion(@PathVariable Long promotionId) {
        var result = promotionService.getPromotionById(promotionId);
        if(result == null) return new ResponseEntity<>("There is no promotion with this ID.", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("getPromotionByCampaign/{promotionId}")
    ResponseEntity<?> getPromotionByCampaign(@PathVariable Long promotionId) {
        var result = promotionService.getPromotionByCampaign(promotionId);
        if(result.size() == 0) return new ResponseEntity<>("Currently, there is no promotion for this campaign.", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    ResponseEntity<?> addPromotion(@RequestBody Promotion promotion) {
        var result = promotionService.addPromotion(promotion);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PutMapping("edit/{promotionId}")
    ResponseEntity<?> editPromotion(
            @PathVariable Long promotionId,
            @RequestParam(required = false) String promotionLink,
            @RequestParam(required = false) String description) {

        var promotion = promotionService.getPromotionById(promotionId);
        if(promotion == null) return new ResponseEntity<>("There is no promotion with this ID.", HttpStatus.NOT_FOUND);

        promotion.setDescription(description != null && description.length() > 0 ? description: promotion.getDescription());
        promotion.setPromotionLink(promotionLink != null && promotionLink.length() > 0 ? promotionLink : promotion.getPromotionLink());

        var result = promotionService.editPromotion(promotion);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("delete/{promotionId}")
    ResponseEntity<?> deletePromotion(@PathVariable Long promotionId) {
        var result = promotionService.getPromotionById(promotionId);
        if (result == null)
            return new ResponseEntity<>("There is no promotion with this ID",HttpStatus.NOT_FOUND);
        promotionService.deletePromotion(promotionId);
        return new ResponseEntity<>("Promotion successfully deleted.",HttpStatus.OK);
    }
}
