package com.jira_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Issue {

    /*
     * These values will be coming from the "getRawIssue" API we hit in JiraService.
     * If response is the returned object...
     */

    private String id; // response.id
    private String key; // response.key
    private String summary; // response.fields.summary
    private String status; // response.fields.statusCategory.name

    /*
    * Create a new Object and set these values
    * response.fields.assignee.displayName
    * response.fields.assignee.accountId
    * response.fields.assignee.emailAddress
    */

    private Assignee assignee;
    private String priority; // response.fields.priority.name
    private String issueType; // response.fields.issueType.name
}
