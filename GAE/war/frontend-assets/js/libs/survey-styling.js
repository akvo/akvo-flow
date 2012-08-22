// Akvo Flow DashBoard Interaction
// loic@akvo.org


$(document).ready(function() {
	$("nav#topnav li.current").prev("nav#topnav li").css("background", "none");
	$("nav#topnav li").hover( function (){
		$(this).prev().css("background", "none");
		})
    // 	Adds needed classes to survey assets as nth-child selectors don't work in ie.
    $('li.aSurvey:nth-child(4n+1)').addClass('firstOfRow');
    $('li.aSurvey:nth-child(4n)').addClass('endOfRow');
    $('table#devicesListTable tbody tr:nth-child(2n)').addClass('even');
    
	$(".questionSetContent div.innerContent").hide();
	var nCount = 0; 
	$(".addQuestion").click( function () {
		nCount++; 
		$(".questionSetContent div.innerContent").fadeIn().css("box-shadow","0 0 3px rgba(0,0,0,0.1)");
		$(this).insertAfter("div.innerContent");
		$("#numberQuestion").text(
			function() {
				if ( nCount < 10 ) {
					$(this).text("0" + nCount);
					}
				else {
					$(this).text(nCount);
					}
				}
		);
		var nQ = parseInt($("#numberQuestion").text());
		$("h1.questionNbr span").text(
			function () {
				if (nQ < 10) {
					$(this).text("0" + nQ);
					}
				else {
					$(this).text(nQ);
					}
				}
		)
		}
	);
// Function displaying the options depending on question type
	$('.formElems').hide();

    // listener for QR type
    $("#questionType").change(function ()

    {
        var selected = $("#questionType option:selected").val();
            $('.formElems').hide();
            $("." +selected).show();
    });
	
// Function displaying the survey groups.
	$("#main > section.surveysList").hide();
	$("#main > section#allSurvey").show();
	$(".menuGroup li a").click(
		function () {
			$(".menuGroup li a").removeClass("current");
			$(this).addClass("current");
			var sectionname = this.name;
			$("#main > section.surveysList").hide();
			$("#"+sectionname).show();
			}
	)
	
// Function displaying the action menu on surveys.
// 	$(".aSurvey nav").hide();
// 	$(".aSurvey").hover(
// 		function () {
// 			$(this).find("nav").fadeToggle(150, "linear");
// 			}
// 	)
});