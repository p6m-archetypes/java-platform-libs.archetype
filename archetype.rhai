let context = #{};

if switch_enabled("debug-answers") {
    log(Info, `${archetect::archetype::description()} Answers:`);
    display(as_yaml(ANSWERS));
}

let archetype_settings = archetect::archetype::settings();

context += render(Archetype("org-prompts"), ANSWERS, archetype_settings);

context += render(Archetype("java-project-attributes"), ANSWERS + context + #{
    "project-prefix": "platform",
    "project-suffix": "libs",
    "artifact-id": "platform",
}, archetype_settings);

context += prompt("Artifactory Host:", "artifactory-host", #{
    defaults_with: "p6m.jfrog.io",
  });

context += set("project-prefix", "platform", #{
    cases: [
        CasedIdentityCasedValue(PROGRAMMING_CASES),
    ],
});

context += set("project-suffix", "libs", #{
    cases: [
        CasedIdentityCasedValue(PROGRAMMING_CASES),
    ],
});

context += set("artifact-id", render("{{ org-solution-name }}-{{ project-prefix }}-{{ project-suffix }}", context), #{
    cases: [
        CasedIdentityCasedValue(PROGRAMMING_CASES),
    ],
});

if switch_enabled("debug-context") {
    log(Info, `${archetect::archetype::description()} (${context["project-name"]}) Context:`);
    display(as_yaml(context));
}

render(Directory("layouts_base"), context);
render(Archetype("gitignore"), context["artifact-id"], #{ "ignore": ["java"]});

// Export Context
context
