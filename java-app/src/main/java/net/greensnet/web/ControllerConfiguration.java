package net.greensnet.web;

import net.greensnet.exceptions.ItemDeletedException;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerConfiguration extends ResponseEntityExceptionHandler {

	private static final String NOW = "now";

	@ModelAttribute
	public void setupAttributes(Model model) {
		model.addAttribute(NOW, LocalDateTime.now());
	}

	@ExceptionHandler(value = { NotFoundException.class })
	protected ResponseEntity<Object> handle404(RuntimeException ex, WebRequest request) {
		return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { ItemDeletedException.class })
	protected ResponseEntity<Object> handle410(RuntimeException ex, WebRequest request) {
		return new ResponseEntity<>("Gone", HttpStatus.GONE);
	}
}
