// Akvo Flow DashBoard Interaction
// loic@akvo.org
this.tooltip = function () {
  /* CONFIG */
  var xOffset = 10;
  var yOffset = 20;
  // these 2 variable determine popup's distance from the cursor
  // you might want to adjust to get the right result
  /* END CONFIG */
  var t = "";

  $("a.tooltip").off("mouseenter").on("mouseenter", function (e) {
    t = this.title;
    this.title = "";
    $("body").append("<p id='tooltip'>" + t + "</p>");
    $("#tooltip").css("top", (e.pageY - xOffset) + "px").css("left", (e.pageX + yOffset) + "px").fadeIn("fast");
  });

  $("a.tooltip").off("mouseleave").on("mouseleave", function () {
    this.title = t;
    $("#tooltip").remove();
  });

  $("a.tooltip").mousemove(function (e) {
    $("#tooltip")
      .css("top", (e.pageY - xOffset) + "px")
      .css("left", (e.pageX + yOffset) + "px");
  });
};

function makePlaceholders() {
    $inputs = $("input[type=text],input[type=email], input[type=tel], input[type=date], input[type=url]");
    $inputs.each(
        function () {
            var $this = jQuery(this);
            this.placeholderVal = $this.attr("placeholder");
            $this.val(this.placeholderVal);
        }
    )

    .bind("focus", function () {
        var $this = jQuery(this);
        var val = $.trim($this.val());
        if (val == this.placeholderVal || val == "") {
            $this.val("");
        }
    })

    .bind("blur", function () {
        var $this = jQuery(this);
        var val = $.trim($this.val());
        if (val == this.placeholderVal || val == "") {
            $this.val(this.placeholderVal);
        }
    });

}

(function ($) {})(window.jQuery);


$(document).ready(function () {

    // TO DELETE ONCE AGREED IT's NOT NECESSARY

    //jQuery to collapse the navbar on scroll
    //$(window).scroll(function() {
    //   if ($(".top").offset().top > 50) {
    //      $(".top").addClass("top-nav-collapse");
    //    $(".belowHeader").css("padding-top", "68px")
    //  $(".leftSidebar").css("top","43px");
    //    } else {
    //      $(".top").removeClass("top-nav-collapse");
    //    $(".belowHeader").css("padding-top", "110px")
    //  $(".leftSidebar").css("top","85px");
    //    }
    //    });


    $("nav#topnav li.current").prev("nav#topnav li").css("background", "none");
    $("nav#topnav li").hover(function () {
        $(this).prev().css("background", "none");
    });
    // Adds needed classes to survey assets as nth-child selectors don't work in ie.
    $('li.aSurvey:nth-child(4n+1)').addClass('firstOfRow');
    $('li.aSurvey:nth-child(4n)').addClass('endOfRow');
    $('table#devicesListTable tbody tr:nth-child(2n)').addClass('even');

    $(".questionSetContent div.innerContent").hide();
    var nCount = 0;
    $(".addQuestion").click(function () {
        nCount++;
        $(".questionSetContent div.innerContent").fadeIn().css("box-shadow", "0 0 3px rgba(0,0,0,0.1)");
        $(this).insertAfter("div.innerContent");
        $("#numberQuestion").text(
            function () {
                if (nCount < 10) {
                    $(this).text("0" + nCount);
                } else {
                    $(this).text(nCount);
                }
            }
        );
        var nQ = parseInt($("#numberQuestion").text());
        $("h1.questionNbr span").text(
            function () {
                if (nQ < 10) {
                    $(this).text("0" + nQ);
                } else {
                    $(this).text(nQ);
                }
            }
        );
    });

    // Function displaying the options depending on question type
    $('.formElems').hide();
    // listener for QR type
    $("#questionType").change(function () {
        var selected = $("#questionType option:selected").val();
        $('.formElems').hide();
        $("." + selected).show();
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
            $("#" + sectionname).show();
        }
    );
    $('#tabs > section').hide();
    $('#tabs > section:first').show();
    $('#tabs ul li:first').addClass('active');
    $('#tabs ul li a').click(function () {
        $('#tabs ul li').removeClass('active');
        $(this).parent().addClass('active');
        var currentTab = $(this).attr('href');
        $('#tabs > section').hide();
        $(currentTab).show();
        return false;
    });
    $("#from").datepicker({
        defaultDate: "+1w",
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
            $("#to").datepicker("option", "minDate", selectedDate);
        }
    });
    $("#to").datepicker({
        defaultDate: "+1w",
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
            $("#from").datepicker("option", "maxDate", selectedDate);
        }
    });
    $('.dataTable').dataTable({
        "aaSorting": [
            [1, "desc"]
        ],
        "sScrollX": "100%",
        "sScrollXInner": "120%",
        "bScrollCollapse": true

    });
    $(".dataTables_paginate").addClass("floats-in");
    $(".dataTables_filter label > input").removeAttr("type").attr("type", "search");


});
