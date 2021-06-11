package org.serratec.com.backend.ecommerce.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.serratec.com.backend.ecommerce.configs.MailConfig;
import org.serratec.com.backend.ecommerce.entities.CarrinhoEntity;
import org.serratec.com.backend.ecommerce.entities.PedidoEntity;
import org.serratec.com.backend.ecommerce.entities.dto.CadastroPedidoDto;
import org.serratec.com.backend.ecommerce.entities.dto.CarrinhoDto;
import org.serratec.com.backend.ecommerce.entities.dto.PedidoDto;
import org.serratec.com.backend.ecommerce.entities.dto.ProdutoDto;
import org.serratec.com.backend.ecommerce.entities.dto.ProdutosPedidosDto;
import org.serratec.com.backend.ecommerce.enums.StatusCompra;
import org.serratec.com.backend.ecommerce.exceptions.CarrinhoException;
import org.serratec.com.backend.ecommerce.exceptions.EntityNotFoundException;
import org.serratec.com.backend.ecommerce.exceptions.PedidoException;
import org.serratec.com.backend.ecommerce.exceptions.ProdutoException;
import org.serratec.com.backend.ecommerce.mappers.PedidoMapper;
import org.serratec.com.backend.ecommerce.mappers.ProdutoMapper;
import org.serratec.com.backend.ecommerce.repositories.CarrinhoRepository;
import org.serratec.com.backend.ecommerce.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	CarrinhoRepository carrinhoRepository;

	@Autowired
	PedidoMapper pedidoMapper;

	@Autowired
	ProdutoMapper produtoMapper;

	@Autowired
	ClienteService clienteService;

	@Autowired
	ProdutoService produtoService;

	@Autowired
	CarrinhoService carrinhoService;


	@Autowired
	MailConfig mailConfig;

	@Autowired
	MailConfig mailconfig;

	public PedidoEntity findById(Long id) throws EntityNotFoundException {
		return pedidoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id + " não encontrado."));
	}

	public List<PedidoDto> getAll() {
		return pedidoRepository.findAll().stream().map(pedidoMapper::toDto).collect(Collectors.toList());
	}

	public PedidoDto getByNumeroPedido(String numeroPedido) throws EntityNotFoundException {
		return pedidoMapper.toDto(pedidoRepository.findByNumeroPedido(numeroPedido));
	}

	public PedidoEntity create(PedidoDto pedidoDto) throws EntityNotFoundException {
		PedidoEntity pedidoEntity = pedidoMapper.toEntity(pedidoDto);
		pedidoEntity.setCliente(clienteService.findById(pedidoDto.getCliente()));

		pedidoDto.setStatus(StatusCompra.NAO_FINALIZADO);

		pedidoDto.setNumeroPedido(this.generateNumber());

		return pedidoRepository.save(pedidoMapper.toEntity(pedidoDto));
	}

	public void delete(Long id) throws EntityNotFoundException, PedidoException {
		if (this.findById(id) != null) {
			if (this.findById(id).getStatus().equals(StatusCompra.NAO_FINALIZADO)) {
				List<CarrinhoEntity> listaCarrinhoEntity = carrinhoRepository.findByPedidos(this.findById(id));
				for (CarrinhoEntity carrinhoEntity : listaCarrinhoEntity) {
					if (carrinhoEntity.getPedidos().getId().equals(id)) {
						carrinhoRepository.delete(carrinhoEntity);
					}
				}
				pedidoRepository.deleteById(id);
			} else {
				throw new PedidoException("Pedido com id: " + id + " já finalizado");
			}
		} else {
			throw new EntityNotFoundException("Pedido com id: " + id + "não encontrado!");
		}

	}

	private String generateNumber() {
		String numeroPedido = "";
		Random number = new Random();
		for (int i = 1; i <= 8; i++) {
			numeroPedido = numeroPedido + number.nextInt(9);
		}
		return numeroPedido;
	}

	public CadastroPedidoDto order(PedidoDto pedidoDto) throws EntityNotFoundException, ProdutoException {
		Long idPedido = this.create(pedidoDto).getId();

		List<ProdutoDto> listaProdutoDto = new ArrayList<>();
		List<CarrinhoDto> listaCarrinhoDto = new ArrayList<>();

		List<ProdutosPedidosDto> listaProdutosPedidosDto = pedidoDto.getProduto();

		for (ProdutosPedidosDto produtosPedidosDto : listaProdutosPedidosDto) {
			ProdutoDto dto = produtoService.getByName(produtosPedidosDto.getNome());
			dto.setQuantidade(produtosPedidosDto.getQuantidade());

			listaProdutoDto.add(dto);
		}

		for (ProdutoDto produtoDto : listaProdutoDto) {
			CarrinhoDto carrinhoDto = new CarrinhoDto();
			carrinhoDto.setPreco(produtoDto.getPreco());

			carrinhoDto.setProduto(produtoService.findByName(produtoDto.getNome()).getId());

			carrinhoDto.setQuantidade(produtoDto.getQuantidade());
			carrinhoDto.setPedido(idPedido);
			listaCarrinhoDto.add(carrinhoDto);
		}

		carrinhoService.create(listaCarrinhoDto);

		PedidoEntity pedidoEntity = this.findById(idPedido);
		pedidoEntity.setValorTotal(carrinhoService.calcularTotal(idPedido));

		pedidoRepository.save(pedidoEntity);

		PedidoDto pedido = pedidoMapper.toDto(pedidoEntity);
		pedido.setProduto(pedidoDto.getProduto());

		return pedidoMapper.toCadastroPedidoDto(pedido);

	}

	public PedidoDto updateOrder(String numeroPedido, List<ProdutosPedidosDto> listaProdutosPedidosDto)
			throws EntityNotFoundException {

		List<CarrinhoDto> listaCarrinhoDto = new ArrayList<>();
		List<ProdutoDto> listaProdutoDto = new ArrayList<>();

		for (ProdutosPedidosDto produtosPedidosDto : listaProdutosPedidosDto) {
			ProdutoDto produtoDto = produtoService.getByName(produtosPedidosDto.getNome());
			produtoDto.setQuantidade(produtosPedidosDto.getQuantidade());

			listaProdutoDto.add(produtoDto);
		}

		for (ProdutoDto produtoDto : listaProdutoDto) {
			CarrinhoDto carrinhoDto = new CarrinhoDto();
			carrinhoDto.setPedido(pedidoRepository.findByNumeroPedido(numeroPedido).getId());
			carrinhoDto.setQuantidade(produtoDto.getQuantidade());
			carrinhoDto.setProduto(produtoService.findByName(produtoDto.getNome()).getId());

			listaCarrinhoDto.add(carrinhoDto);
		}
		carrinhoService.adicionarProduto(listaCarrinhoDto);

		PedidoEntity pedidoEntity = pedidoRepository.findByNumeroPedido(numeroPedido);
		pedidoEntity.setValorTotal(carrinhoService.calcularTotal(pedidoEntity.getId()));

		pedidoRepository.save(pedidoEntity);

		return pedidoMapper.toDto(pedidoEntity);
	}

	public PedidoDto deletarProdutoOrder(String numeroPedido, List<ProdutosPedidosDto> listaProdutosPedidosDto)
			throws EntityNotFoundException, CarrinhoException {
		List<CarrinhoDto> listaCarrinhoDto = new ArrayList<>();
		List<ProdutoDto> listaProdutoDto = new ArrayList<>();

		for (ProdutosPedidosDto produtosPedidosDto : listaProdutosPedidosDto) {
			listaProdutoDto.add(produtoService.getByName(produtosPedidosDto.getNome()));
		}

		for (ProdutoDto produtoDto : listaProdutoDto) {
			CarrinhoDto carrinhoDto = new CarrinhoDto();
			carrinhoDto.setPedido(pedidoRepository.findByNumeroPedido(numeroPedido).getId());
			carrinhoDto.setProduto(produtoService.findByName(produtoDto.getNome()).getId());

			listaCarrinhoDto.add(carrinhoDto);
		}

		carrinhoService.removerProdutoCarrinho(listaCarrinhoDto);

		PedidoEntity pedidoEntity = pedidoRepository.findByNumeroPedido(numeroPedido);
		pedidoEntity.setValorTotal(
				carrinhoService.calcularTotal(pedidoRepository.findByNumeroPedido(numeroPedido).getId()));

		pedidoRepository.save(pedidoEntity);

		PedidoDto pedidoDto = pedidoMapper.toDto(pedidoEntity);
		pedidoDto.setProduto(listaProdutosPedidosDto);

		return pedidoDto;
	}

	public PedidoDto finalizarPedido(String numeroPedido) throws EntityNotFoundException {
		PedidoEntity pedidoEntity = pedidoRepository.findByNumeroPedido(numeroPedido);
		List<ProdutosPedidosDto> listaProdutosPedidosDto = new ArrayList<>();
		List<CarrinhoEntity> listaCarrinhoEntity = carrinhoRepository.findByPedidos(pedidoEntity);

		for (CarrinhoEntity carrinhoEntity : listaCarrinhoEntity) {
			ProdutosPedidosDto produtosPedidosDto = produtoMapper
					.toProdutosPedidos(produtoService.findByName(carrinhoEntity.getProdutos().getNome()));
			produtosPedidosDto.setQuantidade(carrinhoEntity.getQuantidade());

			listaProdutosPedidosDto.add(produtosPedidosDto);
		}

		pedidoEntity.setDataPedido(LocalDate.now());
		pedidoEntity.setDataEntrega(LocalDate.now().plusDays(7));
		pedidoEntity.setStatus(StatusCompra.FINALIZADO);

		String msg = "Recebemos seu pedido";

		mailConfig.sendMail(pedidoEntity.getCliente().getEmail(), "Pedido recebido com sucesso", msg);

		PedidoDto pedidoDto = pedidoMapper.toDto(pedidoEntity);
		pedidoDto.setProduto(listaProdutosPedidosDto);

		pedidoRepository.save(pedidoEntity);
		return (pedidoDto);

	}

}