/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link InMemoryTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryTasksTestCase {

    /**
     * InMemoryTasks returns null if the Task is not found.
     */
    @Test
    public void getsByIdNotFound() {
        final Tasks tasks = new InMemoryTasks(Mockito.mock(Storage.class));
        MatcherAssert.assertThat(
            tasks.getById("missing", "john/test", "github"),
            Matchers.nullValue()
        );
    }

    /**
     * InMemoryTasks can register an Issue, creating a Task out of it.
     */
    @Test
    public void registersNewTask() {
        final Storage storage = new InMemory();
        ProjectManager projectManager = storage
            .projectManagers().pick("github");
        final Project project = storage.projects().register(
            this.mockRepo("mihai/test", "github"), projectManager
        );
        final Issue issue = this.mockIssue(
            "123",
            project.repoFullName(),
            project.provider(),
            Contract.Roles.DEV
        );
        MatcherAssert.assertThat(storage.tasks(), Matchers.iterableWithSize(0));

        final Task registered = storage.tasks().register(issue);

        MatcherAssert.assertThat(registered.issue(), Matchers.is(issue));
        MatcherAssert.assertThat(registered.project(), Matchers.is(project));
        MatcherAssert.assertThat(registered.role(), Matchers.is(issue.role()));
        MatcherAssert.assertThat(registered.assignee(), Matchers.nullValue());

        MatcherAssert.assertThat(
            storage.tasks(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            storage.tasks().getById(
                issue.issueId(), issue.repoFullName(), issue.provider()
            ),
            Matchers.is(registered)
        );
    }

    /**
     * Mock a Repo for test.
     * @param fullName Full name.
     * @param provider Provider.
     * @return Repo.
     */
    private Repo mockRepo(
        final String fullName,
        final String provider
    ) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.fullName()).thenReturn(fullName);
        Mockito.when(repo.provider()).thenReturn(provider);
        return repo;
    }

    /**
     * Mock an Issue for test.
     * @param issueId ID.
     * @param repoFullName Repo fullname.
     * @param provider Provider.
     * @param role Role.
     * @return Issue.
     */
    private Issue mockIssue(
        final String issueId, final String repoFullName,
        final String provider, final String role) {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn(issueId);
        Mockito.when(issue.repoFullName()).thenReturn(repoFullName);
        Mockito.when(issue.provider()).thenReturn(provider);
        Mockito.when(issue.role()).thenReturn(role);
        return issue;
    }

}
