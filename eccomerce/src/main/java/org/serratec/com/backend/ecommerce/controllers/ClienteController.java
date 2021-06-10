package org.serratec.com.backend.ecommerce.controllers;

import java.util.List;

import javax.validation.Valid;

import org.serratec.com.backend.ecommerce.entities.dto.ClienteDto;
import org.serratec.com.backend.ecommerce.entities.dto.ClienteSimplesDto;
import org.serratec.com.backend.ecommerce.exceptions.DataIntegrityViolationException;
import org.serratec.com.backend.ecommerce.exceptions.EntityNotFoundException;
import org.serratec.com.backend.ecommerce.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	ClienteService service;

	@GetMapping
	public ResponseEntity<List<ClienteDto>> getAll() {
		return new ResponseEntity<List<ClienteDto>>(service.getAll(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDto> getById(@PathVariable Long id) throws EntityNotFoundException {
		return new ResponseEntity<ClienteDto>(service.getById(id), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ClienteSimplesDto> create(@Valid @RequestBody ClienteDto dto) throws EntityNotFoundException {
		return new ResponseEntity<ClienteSimplesDto>(service.create(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteDto> update(@PathVariable Long id, @Valid @RequestBody ClienteDto dto)
			throws EntityNotFoundException {
		return new ResponseEntity<ClienteDto>(service.update(id, dto), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) throws EntityNotFoundException, DataIntegrityViolationException {
		service.delete(id);
		return new ResponseEntity<String>("Cliente com " + id +" deletado com sucesso!", HttpStatus.OK);
	}
}