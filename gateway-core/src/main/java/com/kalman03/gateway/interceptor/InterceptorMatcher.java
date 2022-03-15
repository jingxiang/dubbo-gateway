package com.kalman03.gateway.interceptor;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.web.util.pattern.PatternParseException;

import com.kalman03.gateway.http.GatewayHttpRequest;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public final class InterceptorMatcher {

	@Nullable
	private final PatternAdapter[] includePatterns;
	@Nullable
	private final PatternAdapter[] excludePatterns;

	private static PathMatcher pathMatcher = new AntPathMatcher();

	public InterceptorMatcher(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,
			@Nullable PathPatternParser parser) {
		this.includePatterns = PatternAdapter.initPatterns(includePatterns, parser);
		this.excludePatterns = PatternAdapter.initPatterns(excludePatterns, parser);
	}

	
	public boolean matches(GatewayHttpRequest request) {
		String path = request.path();
		if (!ObjectUtils.isEmpty(this.excludePatterns)) {
			for (PatternAdapter adapter : this.excludePatterns) {
				if (adapter.match(path, pathMatcher)) {
					return false;
				}
			}
		}
		if (ObjectUtils.isEmpty(this.includePatterns)) {
			return true;
		}
		for (PatternAdapter adapter : this.includePatterns) {
			if (adapter.match(path, pathMatcher)) {
				return true;
			}
		}
		return false;
	}

	static class PatternAdapter {

		private final String patternString;

		@Nullable
		private final PathPattern pathPattern;

		public PatternAdapter(String pattern, @Nullable PathPatternParser parser) {
			this.patternString = pattern;
			this.pathPattern = initPathPattern(pattern, parser);
		}

		@Nullable
		private static PathPattern initPathPattern(String pattern, @Nullable PathPatternParser parser) {
			try {
				return (parser != null ? parser : PathPatternParser.defaultInstance).parse(pattern);
			} catch (PatternParseException ex) {
				return null;
			}
		}

		public boolean match(String path,PathMatcher pathMatcher) {
			return pathMatcher.match(this.patternString,  path);
		}

		@Nullable
		public static PatternAdapter[] initPatterns(@Nullable String[] patterns, @Nullable PathPatternParser parser) {

			if (ObjectUtils.isEmpty(patterns)) {
				return null;
			}
			return Arrays.stream(patterns).map(pattern -> new PatternAdapter(pattern, parser))
					.toArray(PatternAdapter[]::new);
		}
	}
}
