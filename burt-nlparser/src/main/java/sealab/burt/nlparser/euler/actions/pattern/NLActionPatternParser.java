package sealab.burt.nlparser.euler.actions.pattern;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.Pair;
import org.apache.commons.lang3.StringUtils;
import sealab.burt.nlparser.euler.actions.nl.ActionType;
import sealab.burt.nlparser.euler.actions.nl.NLAction;
import seers.bugreppatterns.utils.SentenceUtils;
import seers.textanalyzer.DependenciesUtils;
import seers.textanalyzer.QuoteProcessor;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public abstract class NLActionPatternParser {

    protected boolean debugEnabled;
    protected ActionType actType;

    public NLActionPatternParser(boolean debugEnabled) {
        super();
        this.debugEnabled = debugEnabled;
    }

    public List<NLAction> parseSentence(Sentence sentence) throws Exception {

        List<Token> tokens = sentence.getTokens();
        if (tokens.get(tokens.size() - 1).getLemma().equals("?")) {
            return null;
        }

        List<NLAction> actions = processSentence(sentence);
        setQuotes(actions, sentence);
        return actions;
    }

    protected abstract List<NLAction> processSentence(Sentence sentence) throws Exception;

    private void setQuotes(List<NLAction> actions, Sentence sentence) {

        if (actions == null) {
            return;
        }

        HashMap<String, List<Sentence>> quotes = sentence.getQuotes();
        if (quotes == null || quotes.isEmpty()) {
            return;
        }

        Set<Entry<String, List<Sentence>>> entrySet = quotes.entrySet();
        for (Entry<String, List<Sentence>> entry : entrySet) {

            String qKey = entry.getKey();
            List<Sentence> stncs = entry.getValue();

            for (NLAction nlAction : actions) {

                String str = nlAction.getObject2();
                if (str != null) {
                    nlAction.setObject2(str.replace(qKey, stncs.get(0).getText()));
                }
                str = nlAction.getObject();
                if (str != null) {
                    nlAction.setObject(str.replace(qKey, stncs.get(0).getText()));
                }
                str = nlAction.getSubject();
                if (str != null) {
                    nlAction.setSubject(str.replace(qKey, stncs.get(0).getText()));
                }
                str = nlAction.getAction();
                if (str != null) {
                    nlAction.setAction(str.replace(qKey, stncs.get(0).getText()));
                }
                str = nlAction.getPreposition();
                if (str != null) {
                    nlAction.setPreposition(str.replace(qKey, stncs.get(0).getText()));
                }
            }
        }

        // --------------------------------

        for (NLAction nlAction : actions) {

            String str = nlAction.getObject2();

            if (str == null) {
                continue;
            }

            if (!str.matches("^" + QuoteProcessor.QUOTE_PREFIX + "\\d+")) {
                continue;
            }

            List<Sentence> stncs = quotes.get(str);
            if (stncs == null) {
                continue;
            }

            Sentence sentence2 = stncs.get(0);
            String text = sentence2.getText();
            nlAction.setObject2(text);
        }
    }

    protected boolean setPrepositionalClause(SemanticGraph dependencies, IndexedWord verbToken, IndexedWord objToken,
                                             NLAction action, Pair<GrammaticalRelation, IndexedWord> nmodToAvoid) {

        Pair<GrammaticalRelation, IndexedWord> objClause = getNounModifier(dependencies, verbToken, objToken,
                nmodToAvoid);

        if (objClause == null) {

            Pair<GrammaticalRelation, IndexedWord> advmodRel = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "advmod");
            if (advmodRel != null && advmodRel.second.tag().equals("RB")) {
                objClause = getNounModifier(dependencies, advmodRel.second, null, nmodToAvoid);
            }

        }

        if (objClause != null) {

            // check for preposition
            if (TextProcessor.checkGeneralPos(objClause.second.tag(), "NN", "JJ", "PRP", "DT", "CD")) {
                Pair<GrammaticalRelation, IndexedWord> prep = DependenciesUtils.getFirstChildByRelation(dependencies,
                        objClause.second, "case");
                if (prep != null) {
                    action.setPreposition(getPreprosition(dependencies, prep.second));
                    action.setObject2(getObject(dependencies, objClause.second));


                    return true;
                }
            } else if (TextProcessor.checkGeneralPos(objClause.second.tag(), "VB")) {
                Pair<GrammaticalRelation, IndexedWord> prep = DependenciesUtils.getFirstChildByRelation(dependencies,
                        objClause.second, "mark");
                if (prep != null) {
                    action.setPreposition(getPreprosition(dependencies, prep.second));
                    action.setObject2(getObject(dependencies, objClause.second));

                    return true;
                }
            }
        }

        return false;
    }

    protected Pair<GrammaticalRelation, IndexedWord> getNounModifier(SemanticGraph dependencies, IndexedWord verbToken,
                                                                     IndexedWord objToken, Pair<GrammaticalRelation,
            IndexedWord> nmodToAvoid) {
        // is there are clause?
        Pair<GrammaticalRelation, IndexedWord> objClause = DependenciesUtils.getFirstChildByRelation(dependencies,
                objToken, "nmod");

        // if there is no clause, try to get a modifier for the verb
        if (objClause == null) {
            List<Pair<GrammaticalRelation, IndexedWord>> objClauses = DependenciesUtils.getChildRelations(dependencies,
                    verbToken, "nmod");

            if (nmodToAvoid != null) {
                objClauses = objClauses.stream().filter(c -> !c.equals(nmodToAvoid)).collect(Collectors.toList());
            }

            objClauses.sort((p1, p2) -> Integer.compare(p1.second.index(), p2.second.index()));

            if (objClauses.size() == 1) {
                objClause = objClauses.get(0);
            } else if (objClauses.size() > 1) {
                Optional<Pair<GrammaticalRelation, IndexedWord>> toObjCl = objClauses.stream()
                        .filter(c -> "to".equals(c.first.getSpecific())).findAny();
                if (toObjCl.isPresent()) {
                    objClause = toObjCl.get();
                } else {
                    objClause = objClauses.get(0);
                }
            }
        }

        if (objClause == null) {
            objClause = DependenciesUtils.getFirstChildByRelation(dependencies, objToken, "acl");
        }

        return objClause;
    }

    private String getPreprosition(SemanticGraph dependencies, IndexedWord prepToken) {

        String[] rels = {"mwe"};

        String expression = getCompoundExpression(dependencies, prepToken, rels, false, false);

        if (expression != null) {
            expression = expression.toLowerCase();
        }

        return expression;
    }

    protected String getObjectNoNeg(SemanticGraph dependencies, IndexedWord objToken) {
        String[] rels = {"amod", "nummod", "compound"};

        return getCompoundExpression(dependencies, objToken, rels, true, false);

    }

    protected String getObject(SemanticGraph dependencies, IndexedWord objToken) {
        String[] rels = {"amod", "nummod", "compound", "neg"
                , "advmod"
        };

        return getCompoundExpression(dependencies, objToken, rels, true, false);

    }

    protected String getCompoundExpression(SemanticGraph dependencies, IndexedWord idxWord, String[] rels,
                                           boolean isRelPrefix, boolean isLemma) {
        if (idxWord == null) {
            return null;
        }

        List<Pair<GrammaticalRelation, IndexedWord>> relations = DependenciesUtils.getChildRelations(dependencies,
                idxWord, rels);

        //consider the advmod "back" and other non-advmod
        relations =
                relations.stream().filter(r -> !r.first.getShortName().equals("advmod") ||
                        (r.first.getShortName().equals("advmod") && r.second.lemma().equals("back")))
                        .collect(Collectors.toList());

        relations.sort(Comparator.comparingInt(p -> p.second.index()));

        String token = idxWord.word();
        if (isLemma) {
            token = idxWord.lemma();
        }

        if (!relations.isEmpty()) {
            List<String> words;
            if (isLemma) {
                words = relations.stream().map(r -> r.second.lemma()).collect(Collectors.toList());
            } else {
                words = relations.stream().map(r -> r.second.word()).collect(Collectors.toList());
            }

            String affix = StringUtils.join(words, " ").trim();
            if (isRelPrefix) {
                return affix + " " + token;
            } else {
                return token + " " + affix;
            }
        }

        return token;
    }

    public ActionType getActType() {
        return actType;
    }

    public void setActType(ActionType actType) {
        this.actType = actType;
    }

    protected String getVerb(SemanticGraph dependencies, IndexedWord verbToken) {
        return getVerb(dependencies, verbToken, true);
    }

    protected String getVerb(SemanticGraph dependencies, IndexedWord verbToken, boolean extractCompoundVerb) {

        if (verbToken == null) {
            return null;
        }

        Pair<GrammaticalRelation, IndexedWord> rel = DependenciesUtils.getFirstChildByRelation(dependencies, verbToken,
                "compound:prt");
        if (rel != null) {
            return (verbToken.lemma() + " " + rel.second.lemma()).toLowerCase();
        }

        String compoundExpression = verbToken.lemma();

        if (extractCompoundVerb) {
            if (verbToken.lemma().equals("go")) {
                String[] rels = {"advmod"};
                List<Pair<GrammaticalRelation, IndexedWord>> relations =
                        DependenciesUtils.getChildRelations(dependencies,
                                verbToken, rels);

                boolean containsBack = relations.stream().anyMatch(r -> r.second.lemma().equals("back"));

                if (!relations.isEmpty() && containsBack) {
                    compoundExpression = getCompoundExpression(dependencies, verbToken, rels, false, true);
                    return compoundExpression.toLowerCase();
                }
            }

            String[] rels = {"amod"};
            compoundExpression = getCompoundExpression(dependencies, verbToken, rels, true, true);

        }

        // rel = DependenciesUtils.getFirstChildByRelation(dependencies,
        // verbToken,
        // "xcomp");
        // if (rel != null) {
        // return verbToken.lemma() + " " + rel.second.lemma();
        // }
        return compoundExpression.toLowerCase();
    }

    protected String getVerbPassive(SemanticGraph dependencies, IndexedWord verbToken) {

        if (verbToken == null) {
            return null;
        }

        List<Pair<GrammaticalRelation, IndexedWord>> relations = DependenciesUtils.getChildRelations(dependencies,
                verbToken, "compound:prt", "xcomp");

        relations.sort((p1, p2) -> Integer.compare(p1.second.index(), p2.second.index()));

        List<String> words = relations.stream().map(r -> r.second.lemma()).collect(Collectors.toList());
        String suffix = StringUtils.join(words, " ").trim();

        if (!relations.isEmpty()) {
            return verbToken.lemma() + " " + suffix;
        }
        return verbToken.lemma();
    }

    protected boolean checkSetOfLemmas(HashMap<String, List<Sentence>> quoteMap, IndexedWord token,
                                       Set<String> lemmas) {

        boolean check = false;

        // check lemma directly
        if (SentenceUtils.stringEqualsToAnyToken(lemmas, token.lemma())) {
            check = true;
        } else {

            // is there are quote?
            List<Sentence> sentences = quoteMap.get(token.word());
            if (sentences != null && !sentences.isEmpty()) {

                // check the quote
                final String quote = sentences.get(0).getText();
                if (quoteMap != null && lemmas.stream().anyMatch(t -> quote.contains(t))) {
                    check = true;
                }
            }
        }
        return check;
    }

    protected boolean checkStringWithSetOfLemmas(HashMap<String, List<Sentence>> quoteMap, Set<String> lemmas,
                                                 String obj2) {

        if (obj2 == null) {
            return false;
        }

        boolean anyMatch = lemmas.stream().anyMatch(obj2::contains);

        if (!anyMatch) {

            // is there are quote?
            List<Sentence> sentences = quoteMap.get(obj2);
            if (sentences != null && !sentences.isEmpty()) {

                // check the quote
                final String quote = sentences.get(0).getText();
                if (quoteMap != null && lemmas.stream().anyMatch(quote::contains)) {
                    anyMatch = true;
                }
            }
        }

        return anyMatch;
    }

    protected void setActionNegation(SemanticGraph dependencies, IndexedWord verbToken, NLAction action) {
        if (verbToken == null) {
            return;
        }
        Pair<GrammaticalRelation, IndexedWord> negRelation = findNegatedRelation(dependencies, verbToken);
        if (negRelation != null) {
            action.setActionNegated(true);
        } else {
            Pair<GrammaticalRelation, IndexedWord> advmod = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "advmod");
            if (advmod != null && advmod.second.lemma().equalsIgnoreCase("longer")) {
                if (DependenciesUtils.getFirstChildByRelation(dependencies,
                        advmod.second, "neg") != null)
                    action.setActionNegated(true);
            }
        }
    }

    public Pair<GrammaticalRelation, IndexedWord> findNegatedRelation(SemanticGraph dependencies,
                                                                      IndexedWord verbToken) {
        return DependenciesUtils.getFirstChildByRelation(dependencies, verbToken, "neg");
    }

    protected boolean isPassiveVoice(SemanticGraph dependencies, IndexedWord verbToken) {

        Pair<GrammaticalRelation, IndexedWord> auxsPassRel = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "auxpass");

        if (auxsPassRel != null) {
            return true;
        }

        Pair<GrammaticalRelation, IndexedWord> subjPassRel = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "nsubjpass");

        if (subjPassRel != null) {
            return true;
        }

        // ----------------------------------

        Pair<GrammaticalRelation, IndexedWord> auxRel = DependenciesUtils.getFirstChildByRelation(dependencies,
                verbToken, "aux");
        if (auxRel != null && auxRel.second.lemma().equals("be")
                && (verbToken.tag().equals("VBN") || verbToken.tag().equals("VBD"))) {
            return true;
        }

        return false;
    }

    protected IndexedWord checkForTryVerb(SemanticGraph dependencies, IndexedWord verbToken) {
        if (verbToken.lemma().equals("try")) {
            Pair<GrammaticalRelation, IndexedWord> child = DependenciesUtils.getFirstChildByRelation(dependencies,
                    verbToken, "xcomp");
            if (child != null) {
                verbToken = child.second;
            }
        }
        return verbToken;
    }

    protected boolean isPersonalPronoun(IndexedWord subjToken) {
        return subjToken.tag().equals("PRP") && (subjToken.lemma().equalsIgnoreCase("I")
                || subjToken.lemma().equalsIgnoreCase("we") || subjToken.lemma().equalsIgnoreCase("you"));
    }

}
