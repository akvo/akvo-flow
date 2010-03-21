<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.gallatinsystems.survey.domain.*"%>
<%@ page import="com.gallatinsystems.survey.dao.*"%>
<%@ page import="java.util.List"%>


<html>
<body>
<%
	SurveyGroupDAO surveyGroupDAO = new SurveyGroupDAO();
	List<SurveyGroup> surveyGroupList = surveyGroupDAO
			.listSurveyGroup();

	SurveyDAO surveyDAO = new SurveyDAO();
	List<SurveyContainer> surveyContainerList = surveyDAO
			.listSurveyContainers();
%>
Create SurveyGroup
<form action="/webapp/surveygroupmanager" method="post">Code: <input
	type="text" name="code"><br>
<input type="text" name="decription"><br>
<input type="hidden" name="action" value="addSurveyGroup" /> <INPUT
	type="submit" value="Send"></form>


<br>
Associate SurveyGroups
<form action="/webapp/surveygroupmanager" method="post">Survey Group
From: <select name="surveyGroupFrom">
	<%
		for (SurveyGroup surveyGroup : surveyGroupList) {
	%><option id="<%=surveyGroup.getCode()%>"><%=surveyGroup.getCode()%></option>
	<%
		}
	%>
</select> <br>
Survey Group To: <select name="surveyGroupTo">
	<%
		for (SurveyGroup surveyGroup : surveyGroupList) {
	%><option id="<%=surveyGroup.getCode()%>"><%=surveyGroup.getCode()%></option>
	<%
		}
	%>
</select><input type="hidden" name="action" value="associateSurveyGroup" /><br>
<INPUT type="submit" value="Send"></form>

</form>

<br>
Associate Survey to SurveyGroups
<form action="/webapp/surveygroupmanager" method="post">Survey Group : <select
	name="surveyGroupId">
	<%
		for (SurveyGroup surveyGroup : surveyGroupList) {
	%><option value="<%=surveyGroup.getKey().getId()%>"><%=surveyGroup.getCode()%></option>
	<%
		}
	%>
</select> <br>
Survey Container : <select name="surveyContainerId">
	<%
		for (SurveyContainer surveyContainer : surveyContainerList) {
	%><option value="<%=surveyContainer.getKey().getId()%>"><%=surveyContainer.getKey().getId()%></option>
	<%
		}
	%>
</select> <input type="hidden" name="action" value="associateSurveyToSurveyGroup" /><br>
<INPUT type="submit" value="Send"></form>

</form>

</body>
</html>