package curso.api.rest.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceRelatorio implements Serializable {

    private static final long serialVersionUID = 1l;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public byte[] gerarRelatorio(String nomeRelatorio, ServletContext servletContext) throws Exception {
        /* Obter conexao com o banco de dados. */
        Connection connection = jdbcTemplate.getDataSource().getConnection();

        /* Carregar o caminho do arquivo de design Jasper. */
        String pathFileJrxml =  Paths.get(".").toAbsolutePath().normalize().toString() +
                "/cursospringrestapi/src/main/java/curso/api/rest/relatorios/"
                + nomeRelatorio + ".jrxml";
        InputStream inputStream = new FileInputStream(pathFileJrxml);
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Gerar o relatorio com os dados e conexao. */
        final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);

        /* Exporta para byte[] o arquivo PDF para fazer o download. */
        byte[] retorno = JasperExportManager.exportReportToPdf(jasperPrint);
        /* Fecha a conexao com o banco de dados. */
        connection.close();
        return retorno;
    }

    public byte[] gerarRelatorioParam(String nomeRelatorio, Map<String, Object> params, ServletContext servletContext) throws Exception {
        /* Obter conexao com o banco de dados. */
        Connection connection = jdbcTemplate.getDataSource().getConnection();

        /* Carregar o caminho do arquivo de design Jasper. */
        String pathFileJrxml =  Paths.get(".").toAbsolutePath().normalize().toString() +
                "/cursospringrestapi/src/main/java/curso/api/rest/relatorios/"
                + nomeRelatorio + ".jrxml";
        InputStream inputStream = new FileInputStream(pathFileJrxml);
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Gerar o relatorio com os dados e conexao. */
        final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

        /* Exporta para byte[] o arquivo PDF para fazer o download. */
        byte[] retorno = JasperExportManager.exportReportToPdf(jasperPrint);
        /* Fecha a conexao com o banco de dados. */
        connection.close();
        return retorno;
    }
}
