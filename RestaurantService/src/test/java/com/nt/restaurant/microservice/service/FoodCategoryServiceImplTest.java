package com.nt.restaurant.microservice.service;

import com.nt.restaurant.microservice.dto.CommonResponse;
import com.nt.restaurant.microservice.dto.FoodCategoryInDTO;
import com.nt.restaurant.microservice.dto.FoodCategoryOutDTO;
import com.nt.restaurant.microservice.entities.FoodCategory;
import com.nt.restaurant.microservice.entities.Restaurant;
import com.nt.restaurant.microservice.exception.ResourceAlreadyExistException;
import com.nt.restaurant.microservice.exception.ResourceNotFoundException;
import com.nt.restaurant.microservice.repository.FoodCategoryRepository;
import com.nt.restaurant.microservice.repository.RestaurantRepository;
import com.nt.restaurant.microservice.serviceimpl.FoodCategoryServiceImpl;
import com.nt.restaurant.microservice.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FoodCategoryServiceImplTest {

  private static final Logger logger = LogManager.getLogger(FoodCategoryServiceImplTest.class);

  @InjectMocks
  private FoodCategoryServiceImpl foodCategoryService;

  @Mock
  private FoodCategoryRepository foodCategoryRepository;

  @Mock
  private RestaurantRepository restaurantRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAddFoodCategory_Success() {
    FoodCategoryInDTO foodCategoryInDTO = new FoodCategoryInDTO();
    foodCategoryInDTO.setRestaurantId(1);
    foodCategoryInDTO.setFoodCategoryName("Test Category");

    Restaurant restaurant = new Restaurant();
    restaurant.setRestaurantId(1);

    FoodCategory foodCategory = new FoodCategory();
    foodCategory.setFoodCategoryId(1);
    foodCategory.setFoodCategoryName("TEST CATEGORY");

    when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
    when(foodCategoryRepository.findByRestaurantIdAndFoodCategoryName(1, "TEST CATEGORY")).thenReturn(Optional.empty());
    when(foodCategoryRepository.save(any(FoodCategory.class))).thenReturn(foodCategory);

    CommonResponse response = foodCategoryService.addFoodCategory(foodCategoryInDTO);

    assertEquals(Constants.FOOD_CATEGORY_ADDED_SUCCESS, response.getMessage());
    verify(foodCategoryRepository).save(any(FoodCategory.class));
  }

  @Test
  public void testAddFoodCategory_RestaurantNotFound() {
    FoodCategoryInDTO foodCategoryInDTO = new FoodCategoryInDTO();
    foodCategoryInDTO.setRestaurantId(1);
    foodCategoryInDTO.setFoodCategoryName("Test Category");

    when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      foodCategoryService.addFoodCategory(foodCategoryInDTO);
    });

    assertEquals("Restaurant not found", exception.getMessage());
  }

  @Test
  public void testUpdateFoodCategory_Success() {
    FoodCategoryInDTO foodCategoryInDTO = new FoodCategoryInDTO();
    foodCategoryInDTO.setRestaurantId(1);
    foodCategoryInDTO.setFoodCategoryName("Sample Category");

    FoodCategory existingCategory = new FoodCategory();
    existingCategory.setFoodCategoryId(1);
    existingCategory.setFoodCategoryName("Test Category");

    FoodCategory updatedCategory = new FoodCategory();
    updatedCategory.setFoodCategoryId(1);
    updatedCategory.setFoodCategoryName("SAMPLE CATEGORY");

    when(foodCategoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
    when(foodCategoryRepository.findByRestaurantIdAndFoodCategoryName(1, "SAMPLE CATEGORY")).thenReturn(Optional.empty());
    when(foodCategoryRepository.save(any(FoodCategory.class))).thenReturn(updatedCategory);

    CommonResponse response = foodCategoryService.updateFoodCategory(1, foodCategoryInDTO);

    assertEquals(Constants.FOOD_CATEGORY_UPDATED_SUCCESS, response.getMessage());
    verify(foodCategoryRepository).save(any(FoodCategory.class));
  }

  @Test
  public void testUpdateFoodCategory_NotFound() {
    FoodCategoryInDTO foodCategoryInDTO = new FoodCategoryInDTO();
    foodCategoryInDTO.setRestaurantId(1);
    foodCategoryInDTO.setFoodCategoryName("Sample Category");

    when(foodCategoryRepository.findById(1)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      foodCategoryService.updateFoodCategory(1, foodCategoryInDTO);
    });

    assertEquals("Food category not found", exception.getMessage());
  }

  @Test
  public void testUpdateFoodCategory_AlreadyExists() {
    FoodCategoryInDTO foodCategoryInDTO = new FoodCategoryInDTO();
    foodCategoryInDTO.setRestaurantId(1);
    foodCategoryInDTO.setFoodCategoryName("Sample Category");

    FoodCategory existingCategory = new FoodCategory();
    existingCategory.setFoodCategoryId(1);
    existingCategory.setFoodCategoryName("Test Category");

    FoodCategory existingCategoryWithName = new FoodCategory();
    existingCategoryWithName.setFoodCategoryId(2);
    existingCategoryWithName.setFoodCategoryName("SAMPLE CATEGORY");

    when(foodCategoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
    when(foodCategoryRepository.findByRestaurantIdAndFoodCategoryName(1, "SAMPLE CATEGORY")).thenReturn(
      Optional.of(existingCategoryWithName));

    ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, () -> {
      foodCategoryService.updateFoodCategory(1, foodCategoryInDTO);
    });

    assertEquals("Food category already exists for this restaurant", exception.getMessage());
  }

  @Test
  public void testGetFoodCategoryByRestaurantId_Success() {
    Integer restaurantId = 1;
    FoodCategory foodCategory = new FoodCategory();
    foodCategory.setFoodCategoryId(1);
    foodCategory.setFoodCategoryName("Test Category");

    List<FoodCategory> foodCategoryList = new ArrayList<>();
    foodCategoryList.add(foodCategory);

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));
    when(foodCategoryRepository.findByRestaurantId(restaurantId)).thenReturn(foodCategoryList);

    List<FoodCategoryOutDTO> result = foodCategoryService.getFoodCategoryByRestaurantId(restaurantId);

    assertFalse(result.isEmpty());
    assertEquals("Test Category", result.get(0).getFoodCategoryName());
  }

  @Test
  public void testGetFoodCategoryByRestaurantId_NotFound() {
    Integer restaurantId = 1;

    when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      foodCategoryService.getFoodCategoryByRestaurantId(restaurantId);
    });

    assertEquals("Restaurant not found", exception.getMessage());
  }
}
