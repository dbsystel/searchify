'use strict';

/**
 * @ngdoc directive
 * @name ExpertFinderApp.directive:autosize
 * @description
 * # autosize
 */
angular.module('ExpertFinderApp')
  .directive('autosize', function () {
    return {
      restrict: 'A',
      link: function postLink(scope, element, attrs) {


	  	function setSizes(){
		  	var windowHeight = jQuery(window).height();
		  	var headerHeight = jQuery('body .container .header').outerHeight();
		  	var resultHeight = windowHeight-headerHeight;

			element.css('height',resultHeight).css('top',headerHeight);
			$('main').css('height', resultHeight).css('top',headerHeight);
		}

		var resizeTimer;

		angular.element($(window)).bind('resize', function(){
		    clearTimeout(resizeTimer);
		    resizeTimer = setTimeout(setSizes(), 100);
		});

		setSizes();

		$(window).keydown(function(e){
			var active = $('.item.selected');
			var newactive;
			var heightDiff = 0;
			var groupTitleDiff = 0;
			var scrollPos = $('.left').scrollTop();

			if (e.keyCode == 40){ //arrow down
				$('.input input').blur();
				if (active.next('.item').length > 0){
					active.next('.item').addClass('selected').trigger('click');
					active.removeClass('selected');
				} 
				else {
					// look for next group
					if (active.parent('.group').next('.group').length > 0){
						active.parent('.group').next('.group').children('.item').first('.item').addClass('selected').trigger('click');
						active.removeClass('selected');
					}
				}
			} 
			else if (e.keyCode == 38) { // arrow up
				$('.input input').blur();
				if (active.prev('.item').length > 0){
					active.prev('.item').addClass('selected').trigger('click');	
					active.removeClass('selected');
				} 
				else {
					//look for previous group
					if (active.parent('.group').prev('.group').length > 0){
						active.parent('.group').prev('.group').children('.item').last('.item').addClass('selected').trigger('click');
						active.removeClass('selected');
					}
				}
			}
		});
		/* Scroll on click-trigger */
		$('body').on('click','.item.selected', function(){					
			$("div.left").stop().animate({scrollTop:$(this).position().top-30}, 'fast', function() {});
		});
		
		$('body').on('click','.item', function(){					
			$("div.left").stop().animate({scrollTop:$(this).position().top-30}, 'fast', function() {});
		});
		
		/* STICKY FUNCTION */
		$('.left').scroll(function(){
			if ($('.left').scrollTop() > 1){								
				var groupTitles = $('.group_title');
				var groupTitlePos = 0;
				groupTitles.each(function(){
					groupTitlePos = $(this).offset().top;
					
					if ((groupTitlePos < 152) && (groupTitlePos > 119) && (!$(this).hasClass('sticky'))){
						if (($(this).parent('.group').prev('.group').children('.group_title').hasClass('sticky')) && (!$('.sticky').hasClass('shiftable'))){
							var posfix = $(this).position().top;
							$('.sticky').addClass('shiftable').css('top', posfix-33);
						}
					}
					
					if ((groupTitlePos < 120) && (!$(this).hasClass('sticky'))){
						$('.sticky').remove();
						var stickyTitle = $(this).clone().addClass('sticky').prependTo($(this).parent('.group'));
					}
										
					if ((groupTitlePos < 152) && (groupTitlePos > 119) && (!$(this).hasClass('sticky')) && ($(this).parent('.group').prev('.group').length == 0)){
						$('.sticky').remove();
					}
				});	
			} 
			else {
				$('.sticky').remove();
			}
		});
		
      }
    };
  });
