package net.greensnet.service;

import net.greensnet.domain.FeaturedItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeaturedItemService {

	boolean createFeaturedItem(MultipartFile multipartFile,
							  int priority,
							  String targetUrl,
							  String altText,
							  boolean active);
	boolean editFeaturedItem(Long id,
							 int priority,
							 String targetUrl,
							 String altText);
	boolean updateImage(Long id,
						MultipartFile multipartFile);
	List<FeaturedItem> getAllFeaturedItems();
	List<FeaturedItem> getAllActiveItems();

	FeaturedItem getItem(long id);
	boolean deactivateItem(long id);
	boolean activateItem(long id);
}
