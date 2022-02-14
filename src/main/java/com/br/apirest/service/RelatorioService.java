package com.br.apirest.service;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public byte[] gerarRelatorio(String nomeRelatorio, ServletContext servletContext) throws Exception {

		// Obter conexão com o banco
		Connection connection = jdbcTemplate.getDataSource().getConnection();

		// Carregar o caminho do arquivo Jasper
		String caminhoJasper = servletContext.getRealPath("relatorio") + File.separator + nomeRelatorio + ".jasper";
		// Gerar o relatorio com os dados e conexão
		JasperPrint print = JasperFillManager.fillReport(caminhoJasper, new HashMap(), connection);

		// Exports para byte o PDF para fazer o download

		byte[] retorno = JasperExportManager.exportReportToPdf(print);

		connection.close();

		return retorno;
	}
}
