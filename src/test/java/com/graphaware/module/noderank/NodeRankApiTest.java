/*
 * Copyright (c) 2013-2016 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.module.noderank;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphaware.test.integration.NeoServerIntegrationTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class NodeRankApiTest extends NeoServerIntegrationTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String neo4jConfigFile() {
        return "int-test-neo4j.properties";
    }

    @Test
    public void shouldRetrieveTopNodes() throws InterruptedException, IOException {
        httpClient.executeCypher(baseUrl(), "CREATE (m:Person {name:'Michal'})-[:FRIEND_OF]->(d:Person {name:'Daniela'})," +
                " (m)-[:FRIEND_OF]->(v:Person {name:'Vojta'})," +
                " (m)-[:FRIEND_OF]->(a:Person {name:'Adam'})," +
                " (m)-[:FRIEND_OF]->(vi:Person {name:'Vince'})," +
                " (m)-[:FRIEND_OF]->(:Person {name:'Luanne'})," +
                " (vi)-[:FRIEND_OF]->(a)," +
                " (d)-[:FRIEND_OF]->(a)," +
                " (d)-[:FRIEND_OF]->(vi)," +
                " (v)-[:FRIEND_OF]->(a)");

        Thread.sleep(20000);

        String s = httpClient.get(baseUrl() + "/graphaware/noderank/noderank/", HttpStatus.OK.value());

        Map<String, Object> first = (Map<String, Object>) new ObjectMapper().readValue(s, List.class).get(0);

        assertEquals(0, first.get("id"));
        assertEquals("Michal", ((Map<String, Object>) first.get("properties")).get("name"));
    }

    @Test
    public void requestToUnknownModuleShouldProduce404() throws InterruptedException {
        httpClient.get(baseUrl() + "/graphaware/noderank/unknown/",  HttpStatus.NOT_FOUND.value());
    }
}
