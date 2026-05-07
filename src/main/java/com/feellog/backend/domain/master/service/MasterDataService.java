package com.feellog.backend.domain.master.service;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.feellog.backend.domain.category.entity.CategoryGroup;
import com.feellog.backend.domain.category.repository.CategoryGroupRepository;
import com.feellog.backend.domain.category.repository.CategoryRepository;
import com.feellog.backend.domain.emotion.entity.EmotionGroup;
import com.feellog.backend.domain.emotion.repository.EmotionGroupRepository;
import com.feellog.backend.domain.emotion.repository.EmotionRepository;
import com.feellog.backend.domain.expense.repository.ExpenseEmotionRepository;
import com.feellog.backend.domain.expense.repository.ExpenseRepository;
import com.feellog.backend.domain.expense.repository.ExpenseSituationTagRepository;
import com.feellog.backend.domain.income.entity.IncomeCategory;
import com.feellog.backend.domain.income.repository.IncomeCategoryRepository;
import com.feellog.backend.domain.master.dto.CategoryDto;
import com.feellog.backend.domain.master.dto.CategoryGroupDto;
import com.feellog.backend.domain.master.dto.EmotionDto;
import com.feellog.backend.domain.master.dto.EmotionGroupDto;
import com.feellog.backend.domain.master.dto.IncomeCategoryDto;
import com.feellog.backend.domain.master.dto.MasterDataResponseDto;
import com.feellog.backend.domain.master.dto.PaymentMethodDto;
import com.feellog.backend.domain.master.dto.SituationTagDto;
import com.feellog.backend.domain.paymentmethod.entity.PaymentMethod;
import com.feellog.backend.domain.paymentmethod.repository.PaymentMethodRepository;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
import com.feellog.backend.domain.situationtag.repository.SituationTagRepository;
import com.feellog.backend.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasterDataService {
	
	private final CategoryGroupRepository categoryGroupRepository;
	private final EmotionGroupRepository emotionGroupRepository;
	private final SituationTagRepository situationTagRepository;
	private final PaymentMethodRepository paymentMethodRepository;
	private final IncomeCategoryRepository incomeCategoryRepository;
	
	@Transactional
	public MasterDataResponseDto getMasterData() {
		List<CategoryGroup> categoryGroups = categoryGroupRepository.findAllWithCategories();
		List<EmotionGroup> emotionGroups = emotionGroupRepository.findAllWithEmotions();
		List<SituationTag> situationTags = situationTagRepository.findAll();
		List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
		List<IncomeCategory> incomeCategories = incomeCategoryRepository.findAll();

		return MasterDataResponseDto.builder()
				.categoryGroups(toCategoryGroupDto(categoryGroups))
				.emotionGroups(toEmotionGroupDto(emotionGroups))
				.situationTags(toSituationTagDto(situationTags))
				.paymentMethods(toPaymentMethodDto(paymentMethods))
				.incomeCategories(toIncomeCategoryDto(incomeCategories))
				.build();
	}
	
	private List<CategoryGroupDto> toCategoryGroupDto(List<CategoryGroup> groups) {
	    return groups.stream()
	            .map(group -> CategoryGroupDto.builder()
	                    .id(group.getId())
	                    .name(group.getName())
	                    .categories(
	                        group.getCategories().stream()
	                            .map(category -> CategoryDto.builder()
	                                    .id(category.getId())
	                                    .name(category.getName())
	                                    .build()
	                            ).toList()
	                    )
	                    .build()
	            ).toList();
	}
	
	private List<EmotionGroupDto> toEmotionGroupDto(List<EmotionGroup> groups) {
	    return groups.stream()
	            .map(group -> EmotionGroupDto.builder()
	                    .id(group.getId())
	                    .name(group.getName())
	                    .emotions(
	                        group.getEmotions().stream()
	                            .map(emotion -> EmotionDto.builder()
	                                    .id(emotion.getId())
	                                    .name(emotion.getName())
	                                    .build()
	                            ).toList()
	                    )
	                    .build()
	            ).toList();
	}
	
	private List<SituationTagDto> toSituationTagDto(List<SituationTag> tags) {
	    return tags.stream()
	            .map(tag -> SituationTagDto.builder()
	                    .id(tag.getId())
	                    .name(tag.getName())
	                    .build()
	            ).toList();
	}
	
	private List<PaymentMethodDto> toPaymentMethodDto(List<PaymentMethod> methods) {
	    return methods.stream()
	            .map(method -> PaymentMethodDto.builder()
	                    .id(method.getId())
	                    .name(method.getName())
	                    .build()
	            ).toList();
	}
	
	private List<IncomeCategoryDto> toIncomeCategoryDto(List<IncomeCategory> categories) {
	    return categories.stream()
	            .map(category -> IncomeCategoryDto.builder()
	                    .id(category.getId())
	                    .name(category.getName())
	                    .build()
	            ).toList();
	}
}
