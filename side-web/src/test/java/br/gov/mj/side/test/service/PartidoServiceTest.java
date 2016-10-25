package br.gov.mj.side.test.service;

import java.io.File;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.web.dao.PartidoDAO;
import br.gov.mj.side.web.service.PartidoService;
import br.gov.mj.side.web.util.Constants;
import br.gov.mj.side.web.util.ResourcesProducer;

@RunWith(Arquillian.class)
public class PartidoServiceTest {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction tx;

    @Inject
    IGenericPersister genericPersister;

    @Inject
    private PartidoService partidoService;

    @Deployment
    public static Archive<?> createDeployment() {
        File[] jars = Maven.configureResolver().withRemoteRepo("artifactory-mj", "http://cgtimaven.mj.gov.br/artifactory/repo/", "default").resolve("br.gov.mj.infra:infra-negocio:1.2.0", "org.jacoco:org.jacoco.core:0.7.5.201505241946", "org.jadira.usertype:usertype.extended:3.2.0.GA")
                .withTransitivity().asFile();

        Archive<?> file = ShrinkWrap.create(WebArchive.class, "side-test.war").addPackages(true, "br.gov.mj.seg.entidades").addPackages(true, "br.gov.mj.apoio.entidades").addPackages(true, "br.gov.mj.side.entidades").addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("init.txt", "init.txt").addAsResource("ufs.csv", "ufs.csv").addAsResource("partidos.csv", "partidos.csv").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsWebInfResource("side-test-ds.xml", "side-test-ds.xml")
                .addAsWebInfResource("test-jboss-web.xml", "jboss-web.xml").addClass(ResourcesProducer.class).addClass(PartidoDAO.class).addClass(PartidoService.class).addClass(Constants.class).addAsLibraries(jars);
        System.out.println(file.toString(true));
        return file;
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1)
    public void testBuscarPeloIdNull() throws Throwable {
        try {
            partidoService.buscarPeloId(null);
        } catch (EJBException e) {
            Throwable th = e.getCause();
            throw th;
        }
    }

    @Test
    @InSequence(2)
    public void testBuscarPorId() {
        PartidoPolitico partidoPolitico = partidoService.buscarPeloId(13l);
        Assert.assertTrue(partidoPolitico.getId().equals(13l));
    }

    @Test
    @InSequence(3)
    public void testBuscarTodos() {
        List<PartidoPolitico> lista = partidoService.buscarTodos();
        Assert.assertEquals(lista.size(), 32);
    }

    @Before
    public void createData() throws Exception {

        // tx.begin();
        // em.createQuery("delete from PartidoPolitico").executeUpdate();
        // tx.commit();
        //
        // String line = "";
        // try (BufferedReader br = new BufferedReader(new
        // InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/partidos.csv"),
        // "UTF-8"))) {
        //
        // while ((line = br.readLine()) != null) {
        // String[] values = line.split(";");
        //
        // PartidoPolitico pp = new PartidoPolitico();
        // pp.setId(new Long(values[0]));
        // pp.setNomePartido(values[1]);
        // pp.setSiglaPartido(values[2]);
        // genericPersister.persist(pp);
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
    }

}
