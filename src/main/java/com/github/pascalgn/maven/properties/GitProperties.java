/*
 * Copyright 2017 Pascal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pascalgn.maven.properties;

import org.codehaus.plexus.logging.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Finds the Git repository based on the current working directory and reads properties about the current HEAD
 */
class GitProperties {
    private final Logger logger;

    public GitProperties(Logger logger) {
        this.logger = logger;
    }

    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            addProperties(map);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading Git properties!", e);
        }
        return map;
    }

    private void addProperties(Map<String, String> map) throws IOException {
        Repository repository = new FileRepositoryBuilder().setWorkTree(new File("."))
                .readEnvironment().findGitDir().setMustExist(true).build();
        logger.debug("Using git repository: " + repository.getDirectory());

        ObjectId head = repository.resolve("HEAD");
        if (head == null) {
            throw new IllegalStateException("No such revision: HEAD");
        }

        String branch = nullToEmpty(repository.getBranch());
        map.put("git.branch", branch);

        String commitId = head.name();
        map.put("git.commit.id", commitId);

        String commitIdAbbrev = repository.newObjectReader().abbreviate(head).name();
        map.put("git.commit.id.abbrev", commitIdAbbrev);
    }

    private static String nullToEmpty(String str) {
        return (str == null ? "" : str);
    }
}
