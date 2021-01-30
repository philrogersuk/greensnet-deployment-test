package net.greensnet.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.greensnet.dao.FeaturedItemRepository;
import net.greensnet.domain.FeaturedItem;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.files.FileService;
import net.greensnet.service.files.ImageSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static net.greensnet.service.files.ImageSize.Type.ORIGINAL;
import static net.greensnet.service.files.ImageSize.Type.WIDTH;

@Component
public class FeaturedItemServiceImpl implements FeaturedItemService {


	private final FeaturedItemRepository featuredItemRepository;
	private final FileService fileService;

	@Autowired
	public FeaturedItemServiceImpl(FeaturedItemRepository featuredItemRepository,
							  FileService fileService) {
		this.featuredItemRepository = featuredItemRepository;
		this.fileService = fileService;
	}

	@Override
	public boolean createFeaturedItem(MultipartFile multipartFile, int priority, String targetUrl, String altText, boolean active) {
		FeaturedItem item = FeaturedItem.builder().priority(priority)
				.targetUrl(targetUrl)
				.altText(altText)
				.active(active)
				.build();
		featuredItemRepository.save(item);

		if (null != multipartFile && !multipartFile.isEmpty()) {
			handleFileUpload(multipartFile, item);
		}

		return true;
	}

	@Override
	public boolean editFeaturedItem(Long id,
									int priority,
									String targetUrl,
									String altText) {
		FeaturedItem item = featuredItemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find sponsor with ID=" + id));

		item.setPriority(priority);
		item.setTargetUrl(targetUrl);
		item.setAltText(altText);
		featuredItemRepository.save(item);

		return true;
	}

	@Override
	public boolean updateImage(Long id,
							   MultipartFile multipartFile) {
		FeaturedItem item = featuredItemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find sponsor with ID=" + id));

		if (null != multipartFile && !multipartFile.isEmpty()) {
			handleFileUpload(multipartFile, item);
		}

		return true;
	}

	@Override
	public List<FeaturedItem> getAllFeaturedItems() {
		return Lists.newArrayList(featuredItemRepository.findAllByOrderByActiveDesc());
	}

	@Override
	public List<FeaturedItem> getAllActiveItems() {
		return featuredItemRepository.findAllByActiveEqualsOrderByPriorityAsc(true);
	}

	@Override
	public FeaturedItem getItem(long id) {
		return featuredItemRepository.findById(id).orElseThrow(NotFoundException::new);
	}

	@Override
	public boolean deactivateItem(long id) {
		FeaturedItem item = featuredItemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find item with ID=" + id));

		item.setActive(false);
		featuredItemRepository.save(item);

		return true;
	}

	@Override
	public boolean activateItem(long id) {
		FeaturedItem item = featuredItemRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find item with ID=" + id));

		item.setActive(true);
		featuredItemRepository.save(item);
		return true;
	}


	private void handleFileUpload(MultipartFile fileStream, FeaturedItem item) {
		//TODO: Validate ratio is 12 x 5

		String filename = "featuredItems/" + item.getId() + "-" + item.getAltText() + "." + Files.getFileExtension(fileStream.getOriginalFilename());
		List<ImageSize> sizes = Lists.newArrayList(new ImageSize(WIDTH, 1200), new ImageSize(ORIGINAL));

		fileService.uploadImage(fileStream, filename, sizes);
		item.setAwsUrl(fileService.getUrl(new ImageSize(WIDTH, 1200).renameFile(filename)).orElse(null));
		featuredItemRepository.save(item);
	}
}
