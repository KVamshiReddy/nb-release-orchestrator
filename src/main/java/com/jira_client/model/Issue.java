package com.jira_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Issue {

    private String id;
    private String key;
    private String summary;
    private String status;
    private Assignee assignee;
    private String priority;
    private String issueType;


}
