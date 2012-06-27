<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.gallatinsystems.survey.domain.*"%>
<%@ page import="com.gallatinsystems.survey.dao.*"%>
<%@ page import="com.gallatinsystems.framework.dao.BaseDAO"%>
<%@ page import="java.util.List"%>


<!--
  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)

  This file is part of Akvo FLOW.

  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
  either version 3 of the License or any later version.

  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU Affero General Public License included below for more details.

  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
-->
<html>
<body>
<%
	BaseDAO<SurveyGroup> surveyGroupDAO = new BaseDAO<SurveyGroup>(SurveyGroup.class);
	List<SurveyGroup> surveyGroupList = surveyGroupDAO.list("all");

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