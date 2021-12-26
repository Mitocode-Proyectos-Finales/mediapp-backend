package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.dto.SignosVitalesDTO;
import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.SignosVitales;
import com.mitocode.service.ISignosVitalesService;


@RestController
@RequestMapping("/signosVitales")
public class SignosVitalesController {

	@Autowired
	private ISignosVitalesService service;
	
	@Autowired
	private ModelMapper mapper;
	
	@GetMapping
	public ResponseEntity<List<SignosVitalesDTO>> listar() throws Exception{				
		List<SignosVitalesDTO> lista = service.listar().stream().map(p -> mapper.map(p, SignosVitalesDTO.class)).collect(Collectors.toList());
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<SignosVitalesDTO> listarPorId(@PathVariable("id") Integer id) throws Exception{
		SignosVitalesDTO dtoResponse;
		SignosVitales obj = service.listarPorId(id); //SignosVitales		

		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}else {
			dtoResponse = mapper.map(obj, SignosVitalesDTO.class); //SignosVitalesDTO
		}
		
		return new ResponseEntity<>(dtoResponse, HttpStatus.OK); 		
	}
	
	@PostMapping
	public ResponseEntity<Void> registrar(@Valid @RequestBody SignosVitalesDTO dtoRequest) throws Exception{
		SignosVitales p = mapper.map(dtoRequest, SignosVitales.class);
		SignosVitales obj = service.registrar(p);
		SignosVitalesDTO dtoResponse = mapper.map(obj, SignosVitalesDTO.class);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSignosVitales()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping
	public ResponseEntity<SignosVitalesDTO> modificar(@RequestBody SignosVitalesDTO dtoRequest) throws Exception {
		SignosVitales pac = service.listarPorId(dtoRequest.getIdSignosVitales());
		
		if(pac == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + dtoRequest.getIdSignosVitales());	
		}
		
		SignosVitales p = mapper.map(dtoRequest, SignosVitales.class);
		 
		SignosVitales obj = service.modificar(p);
		
		SignosVitalesDTO dtoResponse = mapper.map(obj, SignosVitalesDTO.class);
		
		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		SignosVitales pac = service.listarPorId(id);
		
		if(pac == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/hateoas/{id}")
	public EntityModel<SignosVitalesDTO> listarHateoasPorId(@PathVariable("id") Integer id) throws Exception{
		SignosVitales obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		SignosVitalesDTO dto = mapper.map(obj, SignosVitalesDTO.class);
		
		EntityModel<SignosVitalesDTO> recurso = EntityModel.of(dto);
		//localhost:8080/signosVitales/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
		recurso.add(link1.withRel("signos-vitales-recurso1"));
		recurso.add(link2.withRel("signos-vitales-recurso2"));
		
		return recurso;
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Page<SignosVitalesDTO>> listarPageable(Pageable page) throws Exception{
		Page<SignosVitalesDTO> signosVitales = service.listarPageable(page).map(p -> mapper.map(p, SignosVitalesDTO.class));
		
		return new ResponseEntity<>(signosVitales, HttpStatus.OK);
	}
}
