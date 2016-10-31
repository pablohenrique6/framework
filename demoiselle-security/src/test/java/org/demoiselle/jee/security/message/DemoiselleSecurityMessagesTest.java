/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.demoiselle.jee.security.message;

import javax.inject.Inject;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author 70744416353
 */
@RunWith(CdiTestRunner.class)
public class DemoiselleSecurityMessagesTest {

    @Inject
    private DemoiselleSecurityMessages instance;

    public DemoiselleSecurityMessagesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of accessCheckingPermission method, of class
     * DemoiselleSecurityMessages.
     */
    @Test
    public void test11() {
        System.out.println("accessCheckingPermission");
        String operacao = "Teste1";
        String recurso = "Teste2";
        String expResult = "O usuário não tem permissão para executar a ação Teste1 no recurso Teste2";
        String result = instance.accessCheckingPermission(operacao, recurso);
        assertEquals(expResult, result);

    }

    /**
     * Test of accessDenied method, of class DemoiselleSecurityMessages.
     */
    @Test
    public void test12() {
        System.out.println("accessDenied");
        String usuario = "Teste1";
        String operacao = "Teste2";
        String recurso = "Teste3";
        String expResult = "O usuário não possui permissão para executar a ação Teste1 no recurso Teste2";
        String result = instance.accessDenied(usuario, operacao, recurso);
        assertEquals(expResult, result);

    }

    /**
     * Test of userNotAuthenticated method, of class DemoiselleSecurityMessages.
     */
    @Test
    public void test13() {
        System.out.println("userNotAuthenticated");
        String expResult = "Usuário não autenticado";
        String result = instance.userNotAuthenticated();
        assertEquals(expResult, result);

    }

    /**
     * Test of invalidCredentials method, of class DemoiselleSecurityMessages.
     */
    @Test
    public void test14() {
        System.out.println("invalidCredentials");
        String expResult = "Usuário ou senha inválidos";
        String result = instance.invalidCredentials();
        assertEquals(expResult, result);

    }

    /**
     * Test of doesNotHaveRole method, of class DemoiselleSecurityMessages.
     */
    @Test
    public void test15() {
        System.out.println("doesNotHaveRole");
        String role = "Teste1";
        String expResult = "O Usuário não possui a role:Teste1";
        String result = instance.doesNotHaveRole(role);
        assertEquals(expResult, result);

    }

    /**
     * Test of doesNotHavePermission method, of class
     * DemoiselleSecurityMessages.
     */
    @Test
    public void test16() {
        System.out.println("doesNotHavePermission");
        String operacao = "Teste1";
        String recurso = "Teste2";
        String expResult = "O Usuário não possui a permissão para executar a ação Teste1 no recurso Teste2";
        String result = instance.doesNotHavePermission(operacao, recurso);
        assertEquals(expResult, result);

    }

}